import React, { useEffect, useState } from "react";
import ResultMaker from "../../components/ResultMaker/ResultMaker";
import "./Results.css";
import { applHeaders, request } from "../../Utils/MyAxios";
import { getMatchesWithImg } from "../../Utils/Utils";
import Preloader from "../../components/Preloader/Preloader";

function Results() {

    const [results, setResults] = useState(null);

    async function getResults() {
        const matches = await request("GET", "/getAllResults", {}, applHeaders);
        setResults(await Promise.all(matches.data.map(async day => ({
            ...day,
            matches: await getMatchesWithImg(day.matches)
        }))));
    }


    useEffect(() => {
        getResults();
    }, []);

    return (
        <>
            <div className="results_header"><p>Результаты</p></div>
            {results !== null ?
                <div className="results">
                    {results.map((day) =>
                        <ResultMaker day={day} key={day.date} />
                    )
                    }
                </div>
                :
                <Preloader />
            }
        </>
    );
}

export default Results;