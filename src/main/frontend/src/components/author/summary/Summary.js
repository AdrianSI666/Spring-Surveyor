import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useLocation, useNavigate } from 'react-router-dom';

import 'bootstrap/dist/css/bootstrap.min.css';
import './Summary.css';
import { Form, Container, Accordion, Button } from 'react-bootstrap';
import authHeader from '../../../services/auth-header';

const SummaryProfile = () => {
  const localhost = "localhost";
  const port = "8090"
  const navigate = useNavigate();

  let location = useLocation();
  const surveyId = location.state.id;

  const [answerData, setAnswerData] = useState([]);
  const [surveyData, setSurveyData] = useState([]);
  const [unexpectedError, setUnexpectedError] = useState(null);

  const fetchSummarys = () => {
    axios.get(`http://${localhost}:${port}/api/surveys/${surveyId}`, {headers: authHeader()})
    .then(res => {
      setUnexpectedError(null)
      setSurveyData(res.data)
      axios.get(`http://${localhost}:${port}/api/answers/survey/${surveyId}`, {headers: authHeader()})
      .then(res => {
        setUnexpectedError(null)
        setAnswerData(res.data)
      }).catch(err => {
        console.log(err)
        setUnexpectedError(true)
      })
    }).catch(err => {
      console.log(err)
      setUnexpectedError(true)
    })
  }

  useEffect(() => {
    fetchSummarys();
  }, [])

  function restartSurvey() {
    axios.patch(`http://${localhost}:${port}/api/surveys/${surveyId}/restart`, {headers: authHeader()})
      .then(() => {
        navigate(`/restart/${surveyId}`)
      }).catch(() => {
        navigate(`/running/${surveyId}`)
      })

  }

  const getFilteredArr = (answerData) => {
    const map = new Map();
    answerData.forEach(answer => {
      !map.has(answer.question_id)
        ? map.set(answer.question_id, [answer])
        : map.get(answer.question_id).push(answer)
    });
    return [...map];
  }

  const renderAnswers = getFilteredArr(answerData).map(([key, values]) => {
    return (
      <Accordion key={key} defaultActiveKey={['0']}>
        <Accordion.Item>
          <Accordion.Header>{values[0].question_name}</Accordion.Header>
          <Accordion.Body className='row'>
            {values.map((data) => {
              const id2 = data.participant_id
              const id = data.question_id
              const name = data.participant_nickname
              const content = data.content
              return (
                <div className='col' key={id.toString() + "." + id2.toString()}>
                  <h4>{name}</h4>
                  <Form.Control as='textarea' value={content} disabled />
                </div>
              )
            })}
          </Accordion.Body>
        </Accordion.Item>
      </Accordion>
    )
  })

  if (answerData !== undefined) {
    return (
      <Container>
        {unexpectedError && <span>Unexpected error occurred. Please try again.</span>}
        <h4>Name: {surveyData.name}</h4>
        <Button variant='success' onClick={() => restartSurvey()}>RESTART/JOIN IF RUNNING</Button>
        <Form.Control as='textarea' disabled value={surveyData.description} />
        <h4>Time for each question: {surveyData.hours}h, {surveyData.minutes}m, {surveyData.seconds}s</h4>
        <Container>
          {renderAnswers}
        </Container>
      </Container>
    )
  }
  return (
    <Container>
      {unexpectedError && <span>Unexpected error occurred. Please try again.</span>}
    </Container>
  )
}

function Summary() {
  return (
    <div className='summary'>
      <SummaryProfile />
    </div>
  )
}

export default Summary;
