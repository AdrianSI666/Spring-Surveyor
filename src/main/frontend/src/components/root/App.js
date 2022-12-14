import React from 'react';
import { Outlet } from 'react-router-dom';
import Header from '../header/Header';
import './App.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import { GoogleOAuthProvider } from '@react-oauth/google';

function App() {
  return (
   <div className='container'>
    <div className='row'>
      <div className='col-xs-10 col-xs-offset-1'>
        <Header />
      </div>
      <div className='row'>
        <div className='App'>
          <GoogleOAuthProvider clientId={process.env.REACT_APP_CLIENT_ID}>
            <Outlet />
          </GoogleOAuthProvider>
        </div>
      </div>
    </div>
   </div>
  )
}

export default App;
