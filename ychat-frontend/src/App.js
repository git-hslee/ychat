import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Login from './pages/Login/Login';
import Register from './pages/Register/Register';  // 🔹 회원가입 페이지 추가
import Main from './pages/Main/Main';

const App = () => {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />  {/* 🔹 회원가입 페이지 추가 */}
        <Route path="/" element={<Main />} />
      </Routes>
    </Router>
  );
};

export default App;
