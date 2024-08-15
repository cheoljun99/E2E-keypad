//useSecureKeypad.jsx

"use client";

import {useMemo, useState} from 'react';
import axios from "axios";
import {JSEncrypt} from "jsencrypt";

export default function useSecureKeypad() {
  const [keypad, setKeypad] = useState(null);
  const [keyList, setKeyList] = useState([]);
  const [userInput, setUserInput] = useState([]); // 초기 값을 빈 배열로 설정
  


  const getSecureKeypad = async () => {
    try {
      const response = await axios.get('/api/keypad');
      console.log(response);
      
      if (response.data && response.data.keypadImage) {
        setKeyList(response.data.keyList); // 해시값 배열을 상태로 저장
        setKeypad(response.data.keypadImage);
      }
        
    } catch (error) {
      console.error("Error fetching keypad:", error);
    }
  };

  const onKeyPressed = (index) => {
    
    if (userInput.length < 6) {
      console.log("Selected Hash:", keyList[index]); // 디버깅
        setUserInput([...userInput, keyList[index]]);
    }
};

const sendUserInput = async () => {
  try {
      const response = await axios.post('/api/submit', { input: userInput });
      console.log('Input submitted:', response.data);
      alert(userInput);  // 전송 성공 시 alert 창 띄움
      //setUserInput([]);  // 입력 초기화
      window.location.reload();  // 페이지 새로고침
  } catch (error) {
      alert(userInput);  // 전송 실패 시 alert 창 띄움
      console.error('Error submitting input:', error);
      //setUserInput([]);  // 입력 초기화
      window.location.reload();  // 페이지 새로고침
  }
};
 // 입력이 6개가 되면 전송
 if (userInput && userInput.length === 6) {
  sendUserInput();
}

  return {
    states: {
      keypad,
      userInput,
    },
    actions: {
      getSecureKeypad,
      onKeyPressed,
      sendUserInput
    }
  }
}
