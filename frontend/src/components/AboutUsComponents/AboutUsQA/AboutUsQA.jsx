import React from "react";
import "./AboutUsQA.css";

function AboutUsQA(props) {

    return (
        <div className="qa-wrapper">
            <span className="qa-answer qa-text">В: {props.question}?</span>
            <span className="qa-question qa-text">О: {props.answer}</span>
        </div>
    )
}

export default AboutUsQA;