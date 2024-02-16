import React from "react";

function MatchMapScoreMaker(props) {
    return (
        <div className="team" style={props.styleMain}>
            <div className="container" style={{ alignItems: "center", width: "30px", height: "37px" }}>
                <img className="logo_team" src={props.logo} style={{height: props.partType !== "team" ? "auto" : null}}/>
                {props.isPicked ? <div className="pick"><p>ПИК</p></div> : <></>}
            </div>
            <div className="container" style={props.styleChild}>
                <div className={props.nameClass}>
                    <p style={props.nameStyle}>{props.name}</p>
                </div>
                <div className={props.scoreClass}>
                    <p style={props.colorStyle}>
                        {props.score === null ? "-" : props.score}
                    </p>
                </div>
            </div>
        </div>
    );
}

export default MatchMapScoreMaker;