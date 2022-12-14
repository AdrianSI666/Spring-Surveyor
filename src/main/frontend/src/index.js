import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './components/root/App.js';
import reportWebVitals from './reportWebVitals';
import {
  BrowserRouter as Router,
  Routes,
  Route
} from "react-router-dom";
import Login from './components/author/login/Login';
import Profile from './components/author/profile/Profile';
import Survey from './components/author/survey/Survey';
import Join from './components/participant/join/Join';
import Loading from './components/participant/loading/Loading';
import Question from './components/participant/question/Question';
import Running from './components/author/running/Running';
import Result from './components/author/result/Result';
import Answers from './components/participant/answers/Answers';
import Summary from './components/author/summary/Summary';
import Restart from './components/author/restart/Restart';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <Router>
      <Routes>
        <Route path={"/"} element={<App />}>
          <Route path={"join"} element={<Join />} />
          <Route path={"loading/:surveyId/:participantId"} element={<Loading />} />
          <Route path={"questions/survey/:surveyId/participant/:participantId"} element={<Question />} />
          <Route path={"answers/:participantId"} element={<Answers />} />
          <Route path={"login"} element={<Login />} />
          <Route path={"profile"} element={<Profile />} />
          <Route path={"survey/:id"} element={<Survey />} />
          <Route path={"running/:surveyId"} element={<Running />} />
          <Route path={"result/:surveyId"} element={<Result />} />
          <Route path={"summary"} element={<Summary />} />
          <Route path={"restart/:surveyId"} element={<Restart />} />
        </Route>
      </Routes>
    </Router>
  </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
