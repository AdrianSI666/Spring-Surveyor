import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';

import 'bootstrap/dist/css/bootstrap.min.css';
import './Profile.css';
import { Button, Form, Container, Modal } from 'react-bootstrap';
import authHeader from '../../../services/auth-header';

const ProfileProfile = () => {
  const localhost = "localhost";
  const port = "8090"
  const [profileData, setProfileData] = useState([]);
  const [surveyData, setSurveyData] = useState([]);
  const id = sessionStorage.getItem("id");
  const navigate = useNavigate();
  const [modalAddSurvey, setModalAddSurvey] = React.useState(false);
  const [modalEditAccount, setModalEditAccount] = React.useState(false);
  const [name, setName] = useState("");
  const [surname, setSurname] = useState("");
  const [survName, setSurvName] = useState("");
  const [description, setDescription] = useState("");
  const [passcode, setPasscode] = useState("");
  const [hours, setHours] = useState(0);
  const [minutes, setMinutes] = useState(0);
  const [seconds, setSeconds] = useState(0);
  const [editError, setEditError] = useState(null);
  const [unexpectedError, setUnexpectedError] = useState(null);

  useEffect(() => {
    axios.get(`http://${localhost}:${port}/api/users/${id}`, {headers: authHeader()})
      .then(res => {
        setProfileData(res.data)
        setName(res.data.name)
        setSurname(res.data.surname)
        setUnexpectedError(null)
        axios.get(`http://${localhost}:${port}/api/surveys/user/${id}`, {headers: authHeader()})
          .then(response => {
            setUnexpectedError(null)
            setSurveyData(response.data.surveys)
          })
          .catch(err => {
            console.log(err)
            setUnexpectedError(true)
          })
      }).catch(err => {
        console.log(err)
        setUnexpectedError(true)
      })
  }, [id])

    function show() {
      document.getElementById('passexc').style.display = "block";
    }

  function editAccount(e, name, surname){
    e.preventDefault()
    const edditedAccount = {
      name,
      surname
    }
    axios.put(`http://${localhost}:${port}/api/users/${id}`, {headers: authHeader()}, edditedAccount)
    .then(res => {
      setEditError(null)
      setProfileData(res.data)
      setModalEditAccount(false);
    })
    .catch(() => setEditError(true))
  }

  function deleteAccount(e){
    e.preventDefault()
    axios.delete(`http://${localhost}:${port}/api/users/${id}`, {headers: authHeader()})
    .then(() => {
      setUnexpectedError(null)
      navigate("/login")
    })
    .catch(err => {
      console.log(err)
      setUnexpectedError(true)
    })
  }

  function addSurvey(e, name, description, hours, minutes, seconds, passcode) {
    e.preventDefault()
    if (hours === 0 && minutes === 0 && seconds === 0) {
      //some error handling
    } else {
      const authorId = id
      const newSurvey = {
        name,
        description,
        hours,
        minutes,
        seconds,
        passcode,
        authorId
      }
      axios.post(`http://${localhost}:${port}/api/surveys`, newSurvey, {headers: authHeader()})
        .then((res) => {
          setUnexpectedError(null)
          navigate(`/survey/${res.data.surveyId}`)
        }).catch(err => {
          console.log(err)
          setUnexpectedError(true)
        })
      }
    }


  function deleteSurvey(e, surveyId) {
    e.preventDefault()
    axios.delete(`http://${localhost}:${port}/api/surveys/${surveyId}`, {headers: authHeader()})
      .then(() => {
        setUnexpectedError(null)
        setSurveyData(surveyData.filter(item => item.surveyId !== surveyId))
      }).catch(err => {
        console.log(err)
        setUnexpectedError(true)
      })
  }

  const renderSurveys = surveyData.map((data) => {
    const name = data.name
    const description = data.description
    let props = {
      id:data.surveyId
    }
    return (
      <Container key={data.surveyId}>
        <h4>Name: {name}</h4>
        <Form.Control as='textarea' disabled value={description} />
        <Button variant='info'>
          <Link className="nav-link" to="/summary" state={props}>Go to survey details</Link>
        </Button>
        <br/>
        <Button variant='danger' onClick={(e) => deleteSurvey(e, data.surveyId)}>Delete survey</Button>
      </Container>
    )
  })

  return (
    <Container>
      {unexpectedError && <span>Unexpected error occurred. Please try again.</span>}
      <h4>Name: {profileData.name}</h4>
      <h5>Surname: {profileData.surname}</h5>
      <Button variant="info" onClick={() => setModalEditAccount(true)}>
        Edit account
      </Button>
      <Button variant="danger" onClick={(e) => deleteAccount(e)}>
        Delete account
      </Button>
      <br />
      <Button onClick={() => {
        setModalAddSurvey(true)
      }}>Make a survey</Button>
      <h2>Surveys made:</h2>
      <Container>
        {surveyData!= undefined && renderSurveys}
      </Container>
      <Modal
        show={modalAddSurvey}
        onHide={() => setModalAddSurvey(false)}
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
            addSurvey(e, survName, description, hours, minutes, seconds, passcode);
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
            <Form.Group className='mb-3' controlId='formBasicText'>
              <span id='passexc'>Passcode taken</span>
              <Form.Label>Passcode</Form.Label>
              <Form.Control value={passcode} type='text' placeholder='Leave empty if you want it to auto generate' maxLength={8}
                onChange={e => setPasscode(e.target.value)} />
            </Form.Group>
            <Button variant='primary' type='submit'>
              Add
            </Button>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button onClick={() => setModalAddSurvey(false)}>Close</Button>
        </Modal.Footer>
      </Modal>

      <Modal
        show={modalEditAccount}
        onHide={() => setModalEditAccount(false)}
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
            editAccount(e, name, surname);
          }}>
            {editError && <span>Can't change name and surname to already existing one.</span>}
            <Form.Group className='mb-3' controlId='formBasicText'>
              <Form.Label>Name</Form.Label>
              <Form.Control value={name} type='text' placeholder='Edit name'
                onChange={e => setName(e.target.value)} />
            </Form.Group>
            <Form.Group className='mb-3' controlId='formBasicText'>
              <Form.Label>Surname</Form.Label>
              <Form.Control value={surname} type='text' placeholder='Edit surname'
                onChange={e => setSurname(e.target.value)} />
            </Form.Group>
            <Button variant='primary' type='submit'>
              Change
            </Button>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button onClick={() => setModalEditAccount(false)}>Close</Button>
        </Modal.Footer>
      </Modal>
    </Container>
  )
}

function Profile() {
  return (
    <div className='Profile'>
      <ProfileProfile />
    </div>
  )
}

export default Profile;
