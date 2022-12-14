import React, { useState } from 'react';
import axios, { AxiosHeaders } from 'axios';
import { useNavigate } from 'react-router-dom';

import 'bootstrap/dist/css/bootstrap.min.css';
import './Login.css';
import { Button, Container, Form, Modal } from 'react-bootstrap';
import { GoogleLogin, useGoogleLogin } from '@react-oauth/google';
import Header from '../../header/Header';
import { Buffer } from 'buffer';

const LoginProfile = () => {
  const localHost = "localhost";
  const port = "8090";
  const navigate = useNavigate();
    const [name, setName] = useState("");
    const [surname, setSurname] = useState("");
    const [modalAddUser, setModalAddUser] = React.useState(false);
    const [loginError, setLoginError] = useState(null);
    const [addError, setAddError] = useState(null);
    function login(e, name, surname) {
      e.preventDefault()
      axios.get(`http://${localHost}:${port}/api/users/${name}/${surname}`)
        .then((res) => {
          navigate(`/profile/${res.data.id}`)
        })
        .catch(() => {
          setLoginError(true)
        })
    }
  function show(err) {
    document.getElementById('wrong').style.display = "block";
  }

  const googleLogin = useGoogleLogin({
      flow: 'auth-code',
      onSuccess: async (codeResponse) => {
        console.log(codeResponse);
        // sessionStorage.setItem('code', codeResponse.code)
        // const client = process.env.REACT_APP_CLIENT_ID;
        // const secret = process.env.REACT_APP_CLIENT_SECRET;
        // const redirecturl = "http://localhost:3000/profile"
        // const headers = new Headers();
        // headers.set('Content-type', 'aplication/json')
        // headers.set('Authorization', `Basic ${Buffer.from(`${client}:${secret}`).toString('base64')}`)
        // const code = codeResponse.code
        axios.post(`http://${localHost}:${port}/api/users`, codeResponse)
        .then(res => {
          console.log(res)
          sessionStorage.setItem("accessToken", JSON.stringify(res.data.token))
          sessionStorage.setItem("id", res.data.id)
          navigate(`/profile`)
        })
        // console.log(tokens);
      },
      onError: errorResponse => console.log(errorResponse),
    });

  function login(credentialResponse) {
      axios.post(`http://${localHost}:${port}/login/oauth2/code/google`, credentialResponse)
        .then((res) => {
          sessionStorage.setItem("accessToken", JSON.stringify(res.data.credentials))
          console.log(credentialResponse)
          sessionStorage.setItem("id", res.data.id)
          // navigate(`/profile`)
        }).catch(() => {
          setLoginError(true)
        })
        // e.preventDefault()
            // axios.get(`http://${localHost}:${port}/api/users/${name}/${surname}`)
            //   .then((res) => {
            //     navigate(`/profile/${res.data.id}`)
            //   })
            //   .catch((err) => {
            //     show()
            //   })
          }

  // function createAccount(e, name, surname) {
    //   e.preventDefault()
    //   const newUser = {
    //     name,
    //     surname
    //   }
    //   axios.post(`http://${localHost}:${port}/api/users`, newUser)
    //     .then((res) => {
    //       navigate(`/profile/${res.data.id}`)
    //     })
    //     .catch((err) => {
    //       setAddError(true)
    //     })
    // }

  return (
    <Container>
      {loginError==true && <span>Wrong login data</span>}
      <Button onClick={() => googleLogin()}>
        Sign in with Google 🚀{' '}
      </Button>;
      {/* <GoogleLogin
        onSuccess={credentialResponse => {
          login(credentialResponse)
        }}
      onError={() => {
        console.log('Login Failed')
      }}
      /> */}
      {/* <Form onSubmit={(e) => {
        login(e, name, surname);
      }}>
        <Button variant='info' onClick={ () => setModalAddUser(true)}>
          Create account
        </Button>
        <Form.Group className='mb-3' controlId='formBasicText'>
          <Form.Label>Name</Form.Label>
          <Form.Control value={name} type="text" placeholder='Provide name' onChange={e => setName(e.target.value)} />
        </Form.Group>
        <Form.Group className='mb-3' controlId='formBasicText'>
          <Form.Label>Surname</Form.Label>
          <Form.Control value={surname} type="text" placeholder='Provide surname' onChange={e => setSurname(e.target.value)} />
        </Form.Group>
        <Button variant='success' type='submit'>
          Login
        </Button>
      </Form>
      <Modal
        show={modalAddUser}
        onHide={() => setModalAddUser(false)}
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
            createAccount(e, name, surname);
          }}>
            {addError && <span>User with given name and surname already exists chose different name and surname</span>}
            <Form.Group className='mb-3' controlId='formBasicText'>
              <Form.Label>Name</Form.Label>
              <Form.Control value={name} type='text' placeholder='Provide name'
                onChange={e => setName(e.target.value)} />
            </Form.Group>
            <Form.Group className='mb-3' controlId='formBasicText'>
              <Form.Label>Surname</Form.Label>
              <Form.Control value={surname} type='text' placeholder='Provide surname'
                onChange={e => setSurname(e.target.value)} />
            </Form.Group>
            <Button variant='primary' type='submit'>
              Add
            </Button>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button onClick={() => setModalAddUser(false)}>Close</Button>
        </Modal.Footer>
      </Modal> */}
    </Container>
  )
}

function Login() {
  return (
    <div className='Login'>
      <LoginProfile />
    </div>
  )
}

export default Login;
