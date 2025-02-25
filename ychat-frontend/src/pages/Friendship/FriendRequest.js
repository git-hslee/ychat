import React, { useState } from 'react';

const FriendRequest = () => {
  const [receiverId, setReceiverId] = useState('');

  const sendRequest = () => {
    const token = localStorage.getItem('token'); // JWT 토큰 가져오기

    fetch(`http://localhost:8080/api/public/friendships/request?receiverId=${receiverId}`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    })
      .then(response => response.text())
      .then(result => alert(result))
      .catch(error => alert('Error: ' + error));
  };

  return (
    <div>
      <input
        type="text"
        placeholder="친구 ID 입력"
        value={receiverId}
        onChange={(e) => setReceiverId(e.target.value)}
      />
      <button onClick={sendRequest}>친구 요청 보내기</button>
    </div>
  );
};

export default FriendRequest;
