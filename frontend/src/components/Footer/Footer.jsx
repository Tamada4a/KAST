import React from "react";
import './Footer.css'

const Footer = () => (
  <footer >
    <div className="container">
      <div className="grid grid-col-2 footer">
        <div className="contact">
          <address className="contact-address footer-text">
            <a href="mailto:volce.chat@mail.ru">Написать</a>
            <span> нам</span>
          </address>
          <div className="contact-address footer-text"><a href="/about-us">О нас</a></div>
        </div>
        <div className="copyright footer-text">
          <span>{`© ${new Date().getFullYear()} KAST`}</span>
        </div>
      </div>
    </div>
  </footer>
);

export default Footer;