import React from "react";
import "./InfoPage.css"

function InfoPage({ children }) {

    return (
        <div className="window_error">
            <div className="window_error_text">
                {children}
            </div>
            <img src="/img/info_page/plate.svg" alt="Таблица" style={{ height: "320px" }} />
            <img src="/img/info_page/meerkat_on_the_stump.svg" alt="Сурикат на пне" style={{ height: "300px", marginTop: "20px" }} />
        </div>
    )
}

export default InfoPage;