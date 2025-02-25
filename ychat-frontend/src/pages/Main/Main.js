import React, { useEffect, useState } from 'react';
import './Main.css';
import siteImage from '../../images/ychat.webp';

const Main = () => {
  const [userName, setUserName] = useState('');
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [friendId, setFriendId] = useState('');
  const [message, setMessage] = useState('');

  useEffect(() => {
    const token = localStorage.getItem('token');

    if (!token) {
      setIsAuthenticated(false);
      window.location.href = '/login'; // ✅ 토큰이 없으면 로그인 페이지로 이동
      return;
    }

    const verifyToken = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/auth/verify-token', {
          method: 'GET',
          headers: { 'Authorization': `Bearer ${token}` },
        });

        if (response.ok) {
          const data = await response.json();
          console.log("서버 응답:", data);  // ✅ 응답 데이터 확인
          setIsAuthenticated(true);
          setUserName(data.userName); // ✅ 서버에서 받은 userName을 상태로 저장
        } else {
          throw new Error('토큰이 유효하지 않음');
        }
      } catch (error) {
        console.error('토큰 검증 실패:', error);
        localStorage.removeItem('token');
        setIsAuthenticated(false);
        window.location.href = '/login'; // ✅ 유효하지 않은 토큰이면 로그인 페이지로 이동
      }
    };

    verifyToken();
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('token');
    window.location.href = '/login';
  };

  const handleFriendRequest = async () => {
    const token = localStorage.getItem('token');

    try {
      const response = await fetch(`http://localhost:8080/api/friendships/request?receiverId=${friendId}`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });

      const result = await response.text();
      setMessage(result);
      setTimeout(() => setMessage(''), 3000);
      setFriendId('');
      setIsModalOpen(false);
    } catch (error) {
      setMessage('친구 추가 실패');
    }
  };

  return (
    <div className="main-container">
      <header className="header">
        <div className="logo-container">
          <img src={siteImage} alt="사이트 대표 이미지" className="site-logo" />
          <h1 className="site-name">YChat</h1>
        </div>
        <nav className="menu">
          <ul>
            <li onClick={() => setIsModalOpen(true)}>친구 추가</li>
            <li>메뉴 2</li>
            <li>메뉴 3</li>
          </ul>
        </nav>
        <div className="user-info">
          {isAuthenticated ? (
            <>
              <span>{userName}님 환영합니다!</span>
              <button className="logout-button" onClick={handleLogout}>로그아웃</button>
            </>
          ) : (
            <span>로그인 필요</span>
          )}
        </div>
      </header>

      <div className="content">
        <div className="friend-list">
          <h2>친구 목록 (추가 예정)</h2>
        </div>
        <div className="chat-room-list">
          <h2>채팅방 목록 (추가 예정)</h2>
        </div>
      </div>

      {/* 친구 추가 모달 */}
      {isModalOpen && (
        <div className="modal">
          <div className="modal-content">
            <h2>친구 추가</h2>
            <input
              type="text"
              placeholder="친구 ID 입력"
              value={friendId}
              onChange={(e) => setFriendId(e.target.value)}
            />
            <button onClick={handleFriendRequest}>추가</button>
            <button onClick={() => setIsModalOpen(false)}>닫기</button>
            {message && <p>{message}</p>}
          </div>
        </div>
      )}
    </div>
  );
};

export default Main;
