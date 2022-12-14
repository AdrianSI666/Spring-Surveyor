import React from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import { Link } from 'react-router-dom';

function Header(){
    return(
        <nav className="navbar navbar-expand-lg navbar-light bg-light">
        <div className="container-fluid">
            <span className="navbar-brand" href="#">Navigation</span>
            <ul className="navbar-nav">
                <li className="nav-item">
                    <Link className="nav-link" to="/join">Join</Link>
                </li>
            </ul>
            <div className="navbar" id="navbarNav">
                <ul className="navbar-nav">
                    <li className="nav-item">
                    <Link className="nav-link" to="/login">Login</Link>
                    </li>
                </ul>
            </div>
        </div>
        </nav>);
}

export default Header;