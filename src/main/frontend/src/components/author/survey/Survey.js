import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useParams, useNavigate } from 'react-router-dom';

import 'bootstrap/dist/css/bootstrap.min.css';
import './Survey.css';
import { Button, Form, Container, Modal } from 'react-bootstrap';
import authHeader from '../../../services/auth-header';

const SurveyProfile = () => {
  const localhost = "localhost";
  const port = "8090"
  const navigate = useNavigate();

  const [surveyData, setSurveyData] = useState([]);
  const [questionData, setQuestionData] = useState([]);
  const {id}= useParams();
  const [modalEditSurvey, setModalEditSurvey] = React.useState(false);
  const [modalAddQuestion, setModalAddQuestion] = React.useState(false);
  const [modalEditQuestion, setModalEditQuestion] = React.useState(false);
  const [name, setName] = useState("");
  const [content, setContent] = useState("");
  const [survName, setSurvName] = useState("");
  const [description, setDescription] = useState("");
  const [hours, setHours] = useState(0);
  const [minutes, setMinutes] = useState(0);
  const [seconds, setSeconds] = useState(0);
  const [questionId, setQuestionId] = useState(-1);
  
  const [unexpectedError, setUnexpectedError] = useState(null);
  
  useEffect(() => {
    axios.get(`http://${localhost}:${port}/api/surveys/${id}`, {headers: authHeader()})
    .then(res=>{
      setSurveyData(res.data)
      setSurvName(res.data.name)
      setDescription(res.data.description)
      setHours(res.data.hours)
      setMinutes(res.data.minutes)
      setSeconds(res.data.seconds)
      axios.get(`http://${localhost}:${port}/api/questions/survey/${id}`, {headers: authHeader()})
        .then(response=>{
          setUnexpectedError(null)
          setQuestionData(response.data)
        }).catch(err => {
          console.log(err)
          setUnexpectedError(true)
        })
    })
  },[id])

  function startSurvey(){
    axios.put(`http://${localhost}:${port}/api/surveys/${id}/start`, {headers: authHeader()})
    .then(()=>{
      navigate(`/running/${id}`)
    }).catch(err => {
      console.log(err)
      setUnexpectedError(true)
    })
  }


  function editSurvey(e, name, description, hours, minutes, seconds){
    e.preventDefault()
    const editedSurvey = {
      name, 
      description, 
      hours, 
      minutes, 
      seconds
    }
    axios.put(`http://${localhost}:${port}/api/surveys/${id}`, editedSurvey, {headers: authHeader()})
    .then((res) => {
      setUnexpectedError(null)
      setSurveyData(res.data)
    })
    .catch(err => {
      console.log(err)
      setUnexpectedError(true)
    })
  }

  function addQuestion(e, name, content, surveyId){
    e.preventDefault()
    
      const newQuestion = {name,
          content,
          surveyId
        }
      axios.post(`http://${localhost}:${port}/api/questions`, newQuestion, {headers: authHeader()})
      .then((res) => {
        setUnexpectedError(null)
        setQuestionData([...questionData, res.data])
      }).catch(err => {
        console.log(err)
        setUnexpectedError(true)
      })
  }

  function deleteQuestion(e, questionId){
    e.preventDefault()
    axios.delete(`http://${localhost}:${port}/api/questions/${questionId}`, {headers: authHeader()})
    .then(() => {
      setUnexpectedError(null)
      setQuestionData(questionData.filter(item => item.id !== questionId))
    }).catch(err => {
      console.log(err)
      setUnexpectedError(true)
    })
  }

  function editQuestion(e, name, content){
    e.preventDefault()
    const editedQuestion = {
      name, 
      content
    }
    axios.put(`http://${localhost}:${port}/api/questions/${questionId}`, editedQuestion, {headers: authHeader()})
    .then(() => {
      setUnexpectedError(null)
      questionData.forEach(question => {
        if(question.id === questionId){
          question.name = name
          question.content = content
        }
      })
      setQuestionData([...questionData])
    })
    .catch(err => {
      console.log(err)
      setUnexpectedError(true)
    })
  }

  const renderQuestions = questionData.map((data) => {
    const id=data.id
    const name=data.name
    const content=data.content
    return (
      <div key={id}>
        <h4>{name}</h4>
        <Form.Control as='textarea' value={content} disabled />
        <Button variant='danger' onClick={(e) => deleteQuestion(e, id)}>Remove question</Button>
        <Button variant='info' onClick={() => {
          setQuestionId(id)
          setModalEditQuestion(true)
          }}>Edit question</Button>
      </div>
    )
  })

  return (
    <Container>
      {unexpectedError && <span>Unexpected error occurred. Please try again.</span>}
      <h4>Name: {surveyData.name}</h4>
      <Button variant='success' onClick={() => startSurvey()}>START</Button>
      <Form.Control as='textarea' disabled value={surveyData.description}/>
      <h4>Passcode: {surveyData.passcode}</h4>
      <h4>Time for each question: {surveyData.hours}h, {surveyData.minutes}m, {surveyData.seconds}s</h4>
      <Button variant="info" onClick={() => { 
        setModalEditSurvey(true)
      }}>Edit survey</Button>
      <Container></Container>
      <Button onClick={() => { 
        setModalAddQuestion(true)
      }}>Add question to Survey</Button>
      <Container>
        {renderQuestions}
      </Container>
      <Modal
      show={modalAddQuestion}
      onHide={() => setModalAddQuestion(false)}
      size="lg"
      aria-labelledby="contained-modal-title-vcenter"
      centered
      >
        <Modal.Header>
          <Modal.Title id="contained-modal-title-vcenter">
            Create a question
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={(e) => {
            addQuestion(e, name, content, id);
            setModalAddQuestion(false);
          }}>
            <Form.Group className='mb-3' controlId='formBasicText'>
              <Form.Label>Name</Form.Label>
              <Form.Control value = {name} type = 'text' placeholder = 'Give name for question' 
              onChange={e => setName(e.target.value)}/>
            </Form.Group>
            <Form.Group className='mb-3' controlId='formBasicText'>
              <Form.Label>Content</Form.Label>
              <Form.Control value={content} as='textarea' rows="5" placeholder = 'Give question' 
              onChange={e => setContent(e.target.value)}/>
            </Form.Group>
            <Button variant = 'primary' type = 'submit'>
              Add
            </Button>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button onClick={() => setModalAddQuestion(false)}>Close</Button>
        </Modal.Footer>
      </Modal>

      <Modal
      show={modalEditSurvey}
      onHide={() => setModalEditSurvey(false)}
      size="lg"
      aria-labelledby="contained-modal-title-vcenter"
      centered
      >
        <Modal.Header>
          <Modal.Title id="contained-modal-title-vcenter">
            Edit survey
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={(e) => {
            editSurvey(e, survName, description, hours, minutes, seconds);
            setModalEditSurvey(false);
          }}>
            <Form.Group className='mb-3' controlId='formBasicText'>
              <Form.Label>Name</Form.Label>
              <Form.Control value={survName} type='text' placeholder='Give name for survey'
                onChange={e => setSurvName(e.target.value)} />
            </Form.Group>
            <Form.Group className='mb-3' controlId='formBasicText'>
              <Form.Label>Description</Form.Label>
              <Form.Control value={description} as='textarea' rows="5" placeholder='Give surveys description'
                onChange={e => setDescription(e.target.value)} />
            </Form.Group>
            <Form.Group className='mb-3' controlId='formBasicText'>
              <Form.Label>Duration</Form.Label>
              <Form.Group className='mb-3'>
                <Form.Label>Hours</Form.Label>
                <Form.Control value={hours} type='number' max={24} min={0}
                  onChange={e => setHours(e.target.value)} />
                <Form.Label>minutes</Form.Label>
                <Form.Control value={minutes} type='number' max={59} min={0}
                  onChange={e => setMinutes(e.target.value)} />
                <Form.Label>Seconds</Form.Label>
                <Form.Control value={seconds} type='number' max={59} min={0}
                  onChange={e => setSeconds(e.target.value)} />
              </Form.Group>
            </Form.Group>
            <Button variant='primary' type='submit'>
              Change
            </Button>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button onClick={() => setModalEditSurvey(false)}>Close</Button>
        </Modal.Footer>
      </Modal>

      <Modal
      show={modalEditQuestion}
      onHide={() => setModalEditQuestion(false)}
      size="lg"
      aria-labelledby="contained-modal-title-vcenter"
      centered
      >
        <Modal.Header>
          <Modal.Title id="contained-modal-title-vcenter">
            Edit question
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={(e) => {
            editQuestion(e, name, content);
            setModalEditQuestion(false);
          }}>
            <Form.Group className='mb-3' controlId='formBasicText'>
              <Form.Label>Name</Form.Label>
              <Form.Control value = {name} type = 'text' placeholder = 'Give name for question' 
              onChange={e => setName(e.target.value)}/>
            </Form.Group>
            <Form.Group className='mb-3' controlId='formBasicText'>
              <Form.Label>Content</Form.Label>
              <Form.Control value={content} as='textarea' rows="5" placeholder = 'Give question' 
              onChange={e => setContent(e.target.value)}/>
            </Form.Group>
            <Button variant='primary' type='submit'>
              Change
            </Button>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button onClick={() => setModalEditQuestion(false)}>Close</Button>
        </Modal.Footer>
      </Modal>

    </Container>
  )
}

function Survey() {
  return (
        <div className='Survey'>
          <SurveyProfile />
        </div>
  )
}

export default Survey;