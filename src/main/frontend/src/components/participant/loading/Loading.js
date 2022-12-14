import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate, useParams } from 'react-router-dom';

import 'bootstrap/dist/css/bootstrap.min.css';
import './Loading.css';
import { Button, Form, Modal } from 'react-bootstrap';

const LoadingProfile = () => {
  const localHost = "localhost";
  const port = "8090";
  const navigate = useNavigate();
  const {surveyId}= useParams();
  const {participantId}= useParams();

  const [participantData, setParticipantData] = useState([])
  const [surveyData, setSurveyData] = useState([])
  const [nick, setNick] = useState("")

  const [editModal, setEditModal] = React.useState(false);

  const [unexpectedError, setUnexpectedError] = useState(null);
  const [waiting, setWaiting] = useState(null);
  useEffect(() => {

    axios.get(`http://${localHost}:${port}/api/participants/${participantId}`)
    .then(res => {
      setUnexpectedError(null)
      setParticipantData(res.data)
      setNick(res.data.nick)
    }).catch(err => {
      console.log(err)
      setUnexpectedError(true)
    })

    axios.get(`http://${localHost}:${port}/api/surveys/${surveyId}`)
    .then(res => {
      setUnexpectedError(null)
      setSurveyData(res.data)
      setWaiting(true)
    })
    .catch(err => {
      console.log(err)
      setUnexpectedError(true)
    })
    let interval = setInterval(async () => {
      await axios.get(`http://${localHost}:${port}/api/surveys/${surveyId}/status`)
      .then(res => {
        setUnexpectedError(null)
        if(res.data.isStarted){
          clearInterval(interval)
          navigate(`/questions/survey/${surveyId}/participant/${participantId}`)
        }
      }).catch(err => {
        console.log(err)
        setUnexpectedError(true)
      });
    }, 1000)
    return () => {
      clearInterval(interval);
    };
  }, [])

  function editParticipant(e, nick) {
    e.preventDefault()
    const edditedParticipant = {
      nick
    }
    axios.put(`http://${localHost}:${port}/api/participants/${participantId}`, edditedParticipant)
    .then(res => {
      setUnexpectedError(null)
      setParticipantData(res.data)
    })
    .catch(err => {
      console.log(err)
      setUnexpectedError(true)
    })
  }

  return (
    <div>
      {unexpectedError && <span>Unexpected error occurred. Please try again.</span>}
      {waiting && <h1>WAITING FOR SURVEY TO START</h1>}
      <h2>For survey with name: {surveyData.name}</h2>
      <h4>Your nickname: {participantData.nick}</h4>
      <Button variant='info' onClick={() => setEditModal(true)}>Change nickname</Button>
      <Modal
        show={editModal}
        onHide={() => setEditModal(false)}
        size="lg"
        aria-labelledby="contained-modal-title-vcenter"
        centered
      >
        <Modal.Header>
          <Modal.Title id="contained-modal-title-vcenter">
            Create a Survey
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={(e) => {
            editParticipant(e, nick);
            setEditModal(false);
          }}>
            <Form.Group className='mb-3' controlId='formBasicText'>
              <Form.Label>Nickname</Form.Label>
              <Form.Control value={nick} type='text' placeholder='Edit name'
                onChange={e => setNick(e.target.value)} />
            </Form.Group>
            <Button variant='primary' type='submit'>
              Change
            </Button>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button onClick={() => setEditModal(false)}>Close</Button>
        </Modal.Footer>
      </Modal>
    </div>
  )
}

function Loading() {
  return (
    <div className='Join'>
      <LoadingProfile />
    </div>
  )
}

export default Loading;
