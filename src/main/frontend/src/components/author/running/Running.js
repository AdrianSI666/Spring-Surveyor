import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useParams, useNavigate } from 'react-router-dom';

import 'bootstrap/dist/css/bootstrap.min.css';
import './Running.css';
import { Button, Form, Container } from 'react-bootstrap';
import authHeader from '../../../services/auth-header';

const RunningProfile = () => {
  const localhost = "localhost";
  const port = "8090"
  const navigate = useNavigate();
  const { surveyId } = useParams();

  const [questionData, setQuestionData] = useState([]);
  const [answerData, setAnswerData] = useState([]);
  const [timeError, setTimeError] = useState(null);
  const [unexpectedError, setUnexpectedError] = useState(null);
  const fetchRunning = () => {
    axios.get(`http://${localhost}:${port}/api/questions/survey/${surveyId}/next`, {headers: authHeader()})
      .then(res => {
        setTimeError(null)
        setQuestionData(res.data)
        const question = res.data
        const timer = (question.hours*(60*(60*1000))+(question.minutes*(60*1000))+(question.seconds*1000))
        let questionInterval = setInterval( () => {
          showNextQuestion(questionInterval)
        }, timer)
        return () => {
          clearInterval(questionInterval);
        };
      })
      .catch(err =>{
        setTimeError(true)
      })
    }

  function showNextQuestion(questionInterval){
    axios.get(`http://${localhost}:${port}/api/questions/survey/${surveyId}/next`, {headers: authHeader()})
    .then(res => {
      setQuestionData(res.data)
      setAnswerData([])
    })
    .catch(err =>{
      clearInterval(questionInterval)
      axios.patch(`http://${localhost}:${port}/api/surveys/${surveyId}/stop`, {headers: authHeader()})
      .then(() => {
        setUnexpectedError(null)
        navigate(`/result/${surveyId}`)
      })
      .catch(() => setUnexpectedError(true))
    })
  }

  function checkAnswersForQuestion(e, id){
    e.preventDefault()
    axios.get(`http://${localhost}:${port}/api/answers/question/${id}`, {headers: authHeader()})
    .then(res => {
      setTimeError(null)
      setAnswerData(res.data)
    })
    .catch(err => {
      setTimeError(true)
    })
  }

  useEffect(() => {
    fetchRunning();
  }, [])

  const renderAnswers = answerData.map((data) => {
    const id2=data.participant_id
    const id=data.question_id
    const name=data.participant_nickname
    const content=data.content
    return (
      <div key={id.toString() + "." + id2.toString()}>
        <h4>{name}</h4>
        <Form.Control as='textarea' value={content} disabled/>
      </div>
    )
  })

  if(questionData !== undefined){
    return (
      <Container>
        <Container>
          {timeError && <span>Can't get question now. Survey was already stopped</span>}
          <div key={questionData.id}>
            <h4>{questionData.name}</h4>
            <Form.Control as='textarea' value={questionData.content} disabled />
          </div>
        </Container>
        <Button type='primary' onClick={(e) => checkAnswersForQuestion(e, questionData.id)}>Check for answers</Button>
        <Container className='col-lg-4'>
          {renderAnswers}
        </Container>
      </Container>
    )  
  }
  return (
    <Container>
      {timeError && <span>Can't get question now. Survey was already stopped.</span>}
      {unexpectedError && <span>Unexpected error occurred. Please try again.</span>}
    </Container>
  )
}

function Running() {
  return (
    <div className='running'>
      <RunningProfile />
    </div>
  )
}

export default Running;
