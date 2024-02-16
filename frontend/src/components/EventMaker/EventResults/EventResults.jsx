import React from "react";
import ResultMaker from "../../ResultMaker/ResultMaker";
import './EventResults.css';
import "../../../pages/Results/Results.css";

function EventResults(props) {

    return (
        <div>
            <div className="spacer"></div>
            <div className="results">
                {props.results !== null ?
                    props.results.map((day) =>
                        <ResultMaker day={day} key={day.date} />
                    )
                    :
                    <></>
                }
            </div>
        </div>
    );
}

export default EventResults;