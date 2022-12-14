import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useParams, useNavigate } from 'react-router-dom';

import 'bootstrap/dist/css/bootstrap.min.css';
import './Restart.css';
import { Button, Form, Container, Modal } from 'react-bootstrap';

const RestartProfile = () => {
  const localhost = "localhost";
  const port = "8090"
  const [surveyData, setSurveyData] = useState([]);
  const [questionData, setQuestionData] = useState([]);
  const { surveyId } = useParams();
  const [survName, setSurvName] = useState("");
  const [description, setDescription] = useState("");
  const [hours, setHours] = useState(0);
  const [minutes, setMinutes] = useState(0);
  const [seconds, setSeconds] = useState(0);
  const navigate = useNavigate();

  useEffect(() => {
    axios.get(`http://${localhost}:${port}/api/surveys/${surveyId}`)
      .then(res => {
        setSurveyData(res.data)
        setSurvName(res.data.name)
        setDescription(res.data.description)
        setHours(res.data.hours)
        setMinutes(res.data.minutes)
        setSeconds(res.data.seconds)
        axios.get(`http://${localhost}:${port}/api/questions/survey/${surveyId}`)
          .then(response => {
            setQuestionData(response.data)
          })
      })
  }, [surveyId])

  function startSurvey() {
    axios.patch(`http://${localhost}:${port}/api/surveys/${surveyId}/start`)
      .then(() => {
        navigate(`/running/${surveyId}`)
      })
  }

  const renderQuestions = questionData.map((data) => {
    const id = data.id
    const name = data.name
    const content = data.content
    return (
      <div key={id}>
        <h4>{name}</h4>
        <Form.Control as='textarea' value={content} disabled />
      </div>
    )
  })

  return (
    <Container>
      <h4>Name: {surveyData.name}</h4>
      <Button variant='success' onClick={() => startSurvey()}>START</Button>
      <Form.Control as='textarea' disabled value={surveyData.description} />
      <h4>Passcode: {surveyData.passcode}</h4>
      <h4>Time for each question: {surveyData.hours}h, {surveyData.minutes}m, {surveyData.seconds}s</h4>
      <Container>
        {renderQuestions}
      </Container>
    </Container>
  )
}

function Restart() {
  return (
    <div className='Restart'>
      <RestartProfile />
    </div>
  )
}

export default Restart;