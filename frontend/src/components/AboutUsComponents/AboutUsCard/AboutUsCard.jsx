import React from "react";
import "./AboutUsCard.css";

function AboutUsCard({ name, src, children }) {

    return (
        <div className="about-us-card">
            <img src={src} alt={name} />
            <div className="about-us-text-wrapper">
                <span className="about-us-name">{name}</span>
                {children}
            </div>
        </div>
    )
}

export default AboutUsCard;