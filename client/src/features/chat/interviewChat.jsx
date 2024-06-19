import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import { getResumeInfo } from '../../services/getResumeInfoService';
import { saveAnswer } from '../../services/saveAnswerService';
import generateFollowUpQuestion from '../../services/followUpQuestionService';
import { getInterviewInfo } from '../../services/getInterviewInfoService';
import TextInput from '../chat/TextInput';
import VoiceInput from '../chat/VoiceInput';  // VoiceInput 컴포넌트를 import
import AIDialogBox from '../../components/textbox/aidialogBox.jsx';
import { useQuestionID } from '../../context/questionIDContext.js'; // QuestionIDContext를 import

const ChatContainer = styled.div`
  display: flex;
  justify-content: center;
  height: 100%;
  width: 62.5%;
  background-color: white;
`;

const InnerContainer = styled.div`
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 80%;
  overflow: hidden;
  background-color: white;
`;

const MessagesContainer = styled.div`
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 10px;
  overflow-y: auto;
`;

const InterviewChat = ({ interviewId }) => {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const [questions, setQuestions] = useState([]);
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
  const [hasStarted, setHasStarted] = useState(false);
  const [startMessageIndex, setStartMessageIndex] = useState(-1);
  const [messagesWithButtons, setMessagesWithButtons] = useState(new Set());
  const [followUpCreated, setFollowUpCreated] = useState({});
  const [interviewType, setInterviewType] = useState('');
  const { questionID, setQuestionID } = useQuestionID(); // useQuestionID 훅을 사용하여 questionID와 setQuestionID를 가져옴

  useEffect(() => {
    console.log("Interview ID: ", interviewId);
    if (interviewId) {
      const fetchInterviewData = async () => {
        try {
          // 인터뷰 정보를 가져옴
          const interviewInfo = await getInterviewInfo(interviewId);
          setInterviewType(interviewInfo.data.interviewType);
          console.log("Interview Type:", interviewInfo.data.interviewType); // interviewType 콘솔에 출력

          // 이력서 기반의 질문을 가져옴
          const response = await getResumeInfo(interviewId);
          console.log("Fetched questions:", response.data);
          if (response && response.data) {
            setQuestions(response.data);

            // interviewType이 'text'가 아닌 경우 바로 질문을 시작
            if (interviewInfo.data.interviewType !== 'text') {
              setHasStarted(true);
              setStartMessageIndex(0);
              sendNextQuestion(response.data, 0); // 바로 첫 번째 질문을 보냄
            } else {
              // 'text'인 경우 초기 메시지 추가
              setMessages([
                { text: "안녕하세요, AI 면접을 시작합니다.\n준비가 되셨다면 채팅창에 '시작'을 보내주세요 :)", isUser: false, isFollowUp: false, isFollowUpResponse: false, isFollowUpNext: false }
              ]);
            }
          } else {
            throw new Error("No data received");
          }
        } catch (error) {
          console.error('질문을 가져오는 중 오류 발생:', error.message);
          setMessages(prevMessages => [
            ...prevMessages,
            { text: "질문 데이터를 불러오는 중 문제가 발생했습니다. 다시 시도해 주세요.", isUser: false, isFollowUp: false, isFollowUpResponse: false, isFollowUpNext: false }
          ]);
        }
      };
      fetchInterviewData();
    } else {
      setMessages(prevMessages => [
        ...prevMessages,
        { text: "인터뷰 ID가 제공되지 않았습니다.", isUser: false, isFollowUp: false, isFollowUpResponse: false, isFollowUpNext: false }
      ]);
    }
  }, [interviewId]);

  const handleSend = async (transcript = null) => { // handleSend에 transcript 매개변수 추가
    const userInput = transcript || input.trim(); // transcript가 있으면 사용하고, 없으면 기존의 input 사용
    if (userInput) {
      const userMessage = { text: userInput, isUser: true, isFollowUp: false, isFollowUpResponse: false, isFollowUpNext: false };

      const lastMessage = messages[messages.length - 1];
      if (lastMessage && lastMessage.isFollowUp) {
        userMessage.isFollowUpNext = true;
      }

      const newMessages = [...messages, userMessage];
      setMessages(newMessages);
      setInput('');

      if (hasStarted) {
        const currentQuestion = questions[currentQuestionIndex - 1];
        console.log('Current question ID:', currentQuestion.id);

        const audioPath = `audio/${userInput.replace(/\s+/g, '_').toLowerCase()}.mp3`;

        try {
          const response = await saveAnswer(currentQuestion.id, { content: userInput, audioPath: audioPath });
          console.log('Response from saveAnswer:', response);

          setMessagesWithButtons(prevState => new Set([...prevState, newMessages.length - 1]));
        } catch (error) {
          console.error('답변 저장 중 오류 발생:', error.message);
          setMessages(prevMessages => [
            ...prevMessages,
            { text: "답변을 저장하는 중 오류가 발생했습니다. 다시 시도해 주세요.", isUser: false, isFollowUp: false, isFollowUpResponse: false, isFollowUpNext: false }
          ]);
        }
      } else if (userInput === "시작" && !hasStarted) {
        setHasStarted(true);
        setStartMessageIndex(newMessages.length - 1);
        sendNextQuestion(questions, currentQuestionIndex);
      }
    }
  };

  const sendNextQuestion = (questionsList = questions, index = currentQuestionIndex) => {
    if (index < questionsList.length) {
      const newMessage = { text: questionsList[index].content, isUser: false, number: index + 1, isFollowUp: false, isFollowUpResponse: false, isFollowUpNext: false };
      setMessages(prevMessages => [...prevMessages, newMessage]);
      setMessagesWithButtons(prevState => new Set([...prevState, messages.length]));
      setCurrentQuestionIndex(index + 1);

      // 현재 질문의 ID를 전역 상태에 저장
      setQuestionID(questionsList[index].id);
    } else if (hasStarted) {
      setMessages(prevMessages => [
        ...prevMessages,
        { text: "인터뷰가 종료되었습니다. 수고하셨습니다.", isUser: false, isFollowUp: false, isFollowUpResponse: false, isFollowUpNext: false }
      ]);
      setHasStarted(false);
    }
  };

  const handleButtonClick = (index) => {
    setFollowUpCreated(prevState => ({ ...prevState, [index]: true }));
  };

  const handleFollowUpQuestionClick = async (index) => {
    try {
      const currentQuestion = questions[currentQuestionIndex - 1];
      const followUpResponse = await generateFollowUpQuestion(currentQuestion.id);
      console.log('Generated follow-up question:', followUpResponse);

      if (followUpResponse.data && followUpResponse.data.content) {
        setMessages(prevMessages => [
          ...prevMessages,
          { text: followUpResponse.data.content, isUser: false, number: currentQuestionIndex + 1, isFollowUp: true, isFollowUpResponse: false, isFollowUpNext: false }
        ]);
      }
      handleButtonClick(index);

    } catch (error) {
      console.error('꼬리질문 생성 중 오류 발생:', error.message);
      setMessages(prevMessages => [
        ...prevMessages,
        { text: "꼬리질문을 생성하는 중 오류가 발생했습니다. 다시 시도해 주세요.", isUser: false, isFollowUp: false, isFollowUpResponse: false, isFollowUpNext: false }
      ]);
    }
  };

  const handleFollowUpResponse = (index) => {
    setMessages(prevMessages =>
      prevMessages.map((msg, i) =>
        i === index ? { ...msg, isFollowUpResponse: true } : msg
      )
    );
    sendNextQuestion();
  };

  return (
    <ChatContainer>
      <InnerContainer>
        <MessagesContainer>
          {messages.map((message, index) => (
            <AIDialogBox
              key={index}
              text={message.text}
              isUser={message.isUser}
              number={message.isUser ? null : message.number}
              index={index}
              started={hasStarted}
              renderButtons={hasStarted && messagesWithButtons.has(index) && index > startMessageIndex && !followUpCreated[index]}
              onFollowUpQuestionClick={() => handleFollowUpQuestionClick(index)}
              onNextQuestionClick={() => {
                if (messages[index].isFollowUp) {
                  handleFollowUpResponse(index);
                } else {
                  sendNextQuestion();
                  handleButtonClick(index);
                }
              }}
              followUpCreated={followUpCreated[index]}
              isFollowUp={message.isFollowUp}
              isFollowUpResponse={message.isFollowUpResponse}
              isFollowUpNext={message.isFollowUpNext}
              interviewType={interviewType} // interviewType을 전달
            />
          ))}
        </MessagesContainer>
        {/* interviewType에 따라 조건부로 컴포넌트 렌더링 */}
        {interviewType === 'text' ? (
          <TextInput
            input={input}
            setInput={setInput}
            handleSend={handleSend}
          />
        ) : (
          <VoiceInput
            handleSend={handleSend}
            questionID={questionID} // questionID를 VoiceInput에 전달
          />
        )}
      </InnerContainer>
    </ChatContainer>
  );
};

export default InterviewChat;

