import React, { useState, useEffect } from 'react';
import './Register.css';
import siteImage from '../../images/ychat.webp';
import { useNavigate } from 'react-router-dom'; // ✅ useNavigate 추가

const Register = () => {
  const [formData, setFormData] = useState({
    id: '',
    password: '',
    username: '',
    phoneNumber: '',
    address: '',
  });

  const [message, setMessage] = useState({ text: '', type: '' });
  const [showMessage, setShowMessage] = useState(false); // ✅ 메시지 표시 여부 상태 추가
  const navigate = useNavigate(); // ✅ 페이지 이동을 위한 useNavigate 훅 사용

  useEffect(() => {
    
    if (message.text) {
      setShowMessage(true); // ✅ 메시지가 변경되면 보이도록 설정
      setTimeout(() => setShowMessage(false), 3000); // 3초 후 자동으로 메시지 사라짐
    }
  }, [message]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await fetch('http://localhost:8080/api/public/users/signup', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData),
      });

      const result = await response.text();

      if (response.ok) {
        setMessage({ text: '회원가입 성공!', type: 'success' });
        // ✅ 2초 후 로그인 페이지로 이동
        setTimeout(() => {
          navigate('/login');
        }, 2000);
      } else {
        throw new Error(result);
      }
    } catch (error) {
      setMessage({ text: error.message, type: 'error' });
    }
  };

  return (
    <div className="register-container">
      <img src={siteImage} alt="사이트 대표 이미지" className="site-image" />
      <h2>회원가입</h2>
      <form onSubmit={handleSubmit}>
        <input type="text" name="id" placeholder="아이디" onChange={handleChange} required />
        <input type="password" name="password" placeholder="비밀번호" onChange={handleChange} required />
        <input type="text" name="username" placeholder="이름" onChange={handleChange} required />
        <input type="text" name="phoneNumber" placeholder="전화번호" onChange={handleChange} required />
        <input type="text" name="address" placeholder="주소" onChange={handleChange} required />
        <button type="submit">가입하기</button>
      </form>
      {showMessage && (
        <div key={message.text} className={`message ${message.type}`}>
          {message.text}
        </div>
      )}
    </div>
  );
};

export default Register;
