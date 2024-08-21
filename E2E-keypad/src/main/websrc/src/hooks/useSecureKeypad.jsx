//useSecureKeypad.jsx

"use client";

import {useMemo, useState,useEffect} from 'react';
import axios from "axios";
import {JSEncrypt} from "jsencrypt";

export default function useSecureKeypad() {
  const [keypad, setKeypad] = useState(null);
  const [keypadId, setKeypadId] = useState(null);
  const [keyList, setKeyList] = useState([]);
  const [userInput, setUserInput] = useState([]); // 초기 값을 빈 배열로 설정
  const [publicKey, setPublicKey] = useState(null);


  useEffect(() => {
    fetch('/public_key.pem')
      .then(response => response.text())
      .then(data => {
        setPublicKey(data);

      })
      .catch(error => {
        console.error('Error fetching public key:', error);
      });
  }, []);



  const getSecureKeypad = async () => {
    try {
      const response = await axios.get('/api/keypad');
      console.log(response);
      
      if (response.data && response.data.keypadImage) {
        setKeypadId(response.data.id);
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
  const encrypt = new JSEncrypt();
  const userData = userInput.join('');
  encrypt.setPublicKey(publicKey);
  const encryptedUserData = encrypt.encrypt(userData);
  console.log(encryptedUserData);
  

  try {
    // 페이로드 구성
    const payload = {
        keypadId: keypadId, // 이전에 받은 키패드 아이디를 사용
        userInput: encryptedUserData
    };

    // 서버로 POST 요청 전송
    const response = await axios.post('/api/submit', payload);

    // 응답 확인
    if (response.status === 200) {
        console.log('Data sent successfully');
        console.log(response);
        alert(response.data.result);
        window.location.reload();  // 페이지 새로고침
    } else {
        console.error('Failed to send data', response.statusText);
    }

} catch (error) {
    console.error('Error sending data', error);
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
