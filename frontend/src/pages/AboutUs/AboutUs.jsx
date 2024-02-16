import React from "react";
import "./AboutUs.css";
import AboutUsCard from "../../components/AboutUsComponents/AboutUsCard/AboutUsCard";
import AboutUsQA from "../../components/AboutUsComponents/AboutUsQA/AboutUsQA";

function AboutUs() {
    return (
        <div className="about-us-main">
            <div className="col_center_gap5">
                <span className="about-us-label">Команда создателей</span>
                <div className="about-us-cards-wrapper display-row-center">
                    <AboutUsCard src="img/about_us/Kirill.png" name="Кирилл Симовин">
                        <span className="about-us-info">
                            Fullstack-разработчик,<br />
                            автор идеи,<br />
                            веб-дизайнер
                        </span>
                    </AboutUsCard>
                    <AboutUsCard src="img/about_us/Aleksandr.png" name="Александр Федякин">
                        <span className="about-us-info">
                            Frontend-разработчик,<br />
                            автор идеи
                        </span>
                    </AboutUsCard>
                    <AboutUsCard src="img/about_us/Viktoria.png" name="Виктория Кошевец">
                        <span className="about-us-info">
                            Нарисовала прекрасного<br />
                            суриката
                        </span>
                    </AboutUsCard>
                    <AboutUsCard src="img/about_us/Vadim.png" name="Вадим Савельев">
                        <span className="about-us-info">
                            Заложил цветовую палитру
                        </span>
                    </AboutUsCard>
                </div>
            </div>
            <div className="col_center_gap5">
                <span className="about-us-label">Часто задаваемые вопросы</span>
                <div className="about-us-faq">
                    <AboutUsQA question="Почему KAST" answer="KAST является сокращением от Kirill And Sanya Translation." />
                    <AboutUsQA question="Что означает логотип" answer="Логотип ничего не означает - он был сгенерирован нейросетью." />
                    <AboutUsQA question="Как возник сайт" answer="Сайт возник как проект для одного из курсов в университете: Саша предложил сделать что-то для турниров в компьютерном клубе, а Кирилл вспомнил, что когда-то планировал создать свой HLTV для своих турниров - так и получилось." />
                    <AboutUsQA question="Получается, вы своровали сайт" answer="Надеемся, что это не так. Мы действительно вдохновлялись HLTV, поскольку это единственный ресурс, делающий упор на просмотр матчей и совмещающий в себе информацию о турнирах, игроках и много чего ещё. Да, мы сохранили структуру HLTV, но при этом привнесли в проект и своё виденье - сайт совмещает в себе не только описанные выше возможности HLTV, но и возможность создания команд и участия в турнирах." />
                    <AboutUsQA question="Какую цель преследует KAST" answer="Мы хотим выйти на рынок любительских турниров и повысить их уровень. Хотим, чтобы игроки-любители прикоснулись к уровню организации профессиональных турниров. " />
                </div>
            </div>
        </div>
    )
}

export default AboutUs;