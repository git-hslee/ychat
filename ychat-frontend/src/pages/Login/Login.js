import React, { useState, useEffect } from 'react';
import './Login.css'; // 
import siteImage from '../../images/ychat.webp'; // ✅ 사이트 대표 이미지
import { useNavigate } from 'react-router-dom';  // ✅ useNavigate 추가

const Login = () => {
    const navigate = useNavigate(); // ✅ 페이지 이동을 위한 useNavigate 훅 사용

  const [formData, setFormData] = useState({
    id: '',
    password: '',
  });

  const [message, setMessage] = useState({ text: '', type: '' });

  useEffect(() => {
    if (message.text) {
      setTimeout(() => setMessage({ text: '', type: '' }), 1000); // 1초 후 메시지 사라짐
    }
  }, [message]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await fetch('http://localhost:8080/api/public/users/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData),
      });

      const result = await response.json();

      if (response.ok) {
        localStorage.setItem('token', result.token); // ✅ JWT 토큰 저장
        setMessage({ text: '로그인 성공!', type: 'success' });

        
        setTimeout(() => {
          window.location.href = '/'; // ✅ 로그인 후 메인 페이지로 이동
        }, 2000);
        
      } else {
        throw new Error(result.message || '로그인 실패');
      }
    } catch (error) {
      setMessage({ text: error.message, type: 'error' });
    }
  };

  return (
    <div className="login-container">
      <img src={siteImage} alt="사이트 대표 이미지" className="site-image" />
      <h2>로그인</h2>
      <form onSubmit={handleSubmit}>
        <input type="text" name="id" placeholder="아이디" onChange={handleChange} required />
        <input type="password" name="password" placeholder="비밀번호" onChange={handleChange} required />
        <button type="submit">로그인</button>
      </form>
      {message.text && (
        <div className={`message ${message.type}`}>
          {message.text}
        </div>
      )}
      {/* 🔹 회원가입 버튼 추가 */}
      <p className="register-link">
        아직 계정이 없으신가요?{' '}
        <button onClick={() => navigate('/register')}>회원가입</button>
      </p>
    </div>
  );
};

export default Login;
