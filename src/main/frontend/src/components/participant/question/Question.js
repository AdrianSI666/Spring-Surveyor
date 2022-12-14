import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useParams, useNavigate } from 'react-router-dom';

import 'bootstrap/dist/css/bootstrap.min.css';
import './Question.css';
import { Button, Form, Container } from 'react-bootstrap';

const QuestionProfile = () => {
  const localhost = "localhost";
  const port = "8090"

  const { surveyId } = useParams();
  const { participantId } = useParams();

  const [questionData, setQuestionData] = useState([]);
  const [content, setContent] = useState("");
  
  const navigate = useNavigate();
  const fetchQuestions = () => {
    axios.get(`http://${localhost}:${port}/api/questions/survey/${surveyId}/next`)
      .then(res => {
        setQuestionData(res.data)
        const question = res.data
        let interval = setInterval( () => {
          showNextQuestion(interval)
        }, 
        (question.hours*(60*(60*1000))+(question.minutes*(60*1000))+(question.seconds*1000)))
        return () => {
          clearInterval(interval);
        };
      })
      .catch(err =>{
        document.getElementById('error').style.display = 'block'
        document.getElementById('error').innerHTML = err.response.data.message
      })
    }

  function showNextQuestion(interval){
    axios.get(`http://${localhost}:${port}/api/questions/survey/${surveyId}/next`)
    .then(res => {
      setQuestionData(res.data)
      setContent("")
    })
    .catch(err =>{
      clearInterval(interval)
      navigate(`/answers/${participantId}`)
    })
  }

  useEffect(() => {
    fetchQuestions();
  }, [])

  function addAnswer(e, answer, questionId){
    e.preventDefault()
    const newAnswer = {
      answer,
      questionId,
      participantId
    }
    axios.post(`http://${localhost}:${port}/api/answers`, newAnswer)
    .then(res => {
      console.log(res)
    })
  }

  if(questionData !== undefined){
    return (
      <Container>
        <Container>
          <span id='error' className='hidden'></span>
          <div key={questionData.id}>
            <h4>{questionData.name}</h4>
            <Form.Control as='textarea' value={questionData.content} disabled />
          </div>
        </Container>
        <Form onSubmit={(e) => {
            addAnswer(e, content, questionData.id);
          }} id="give">
            <Form.Group className='mb-3' controlId='formBasicText'>
              <Form.Label>Answer</Form.Label>
              <Form.Control value={content} as='textarea' rows="5" placeholder = 'Give answer' 
              onChange={e => setContent(e.target.value)}/>
            </Form.Group>
            <Button variant = 'primary' type = 'submit'>
              Give answer
            </Button>
        </Form>
      </Container>
    )  
  }
  return (
    <Container>
    </Container>
  )
}

function Question() {
  return (
    <div className='question'>
      <QuestionProfile />
    </div>
  )
}

export default Question;
