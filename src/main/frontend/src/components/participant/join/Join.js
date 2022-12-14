import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

import 'bootstrap/dist/css/bootstrap.min.css';
import './Join.css';
import { Button, Form } from 'react-bootstrap';

const JoinProfile = () => {
  const localHost = "localhost";
  const port = "8090";
  const navigate = useNavigate();
  const [nick, setNick] = useState("");
  const [passcode, setPasscode] = useState("");
  const [loadingError, setLoadingError] = useState(null);
  function join(e, nick, passcode) {
    e.preventDefault()
    const newParticipant = {
      nick,
      passcode
    }
    axios.post(`http://${localHost}:${port}/api/participants`, newParticipant)
    .then((res) => {
      navigate(`/loading/${res.data.surveyId}/${res.data.id}`)
    })
    .catch(() => {
      setLoadingError(true)
    })
  }
  return (
    <Form onSubmit={(e) => {
      join(e, nick, passcode);
    }}>
      {loadingError && <span>Can't join a survey. Try again later.</span>}
      <Form.Group className='mb-3' controlId='formBasicText'>
        <Form.Label>Nickname</Form.Label>
        <Form.Control value={nick} type="text" placeholder='Provide name' onChange={e => setNick(e.target.value)} />
      </Form.Group>
      <Form.Group className='mb-3' controlId='formBasicText'>
        <Form.Label>Passcode</Form.Label>
        <Form.Control value={passcode} type="text" placeholder='Provide 8 letter passcode' onChange={e => setPasscode(e.target.value)} maxLength={8} />
      </Form.Group>
      <Button variant='success' type='submit'>
        Join
      </Button>
    </Form>
  )
}

function Join() {
  return (
    <div className='Join'>
      <JoinProfile />
    </div>
  )
}

export default Join;
