import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import ResultMaker from "../../components/ResultMaker/ResultMaker";
import { applHeaders, request } from "../../Utils/MyAxios";
import { unFillSpaces, getMatchesWithImg } from "../../Utils/Utils";
import "../Results/Results.css";
import Preloader from "../../components/Preloader/Preloader";


function TeamResults() {
    const [results, setResults] = useState(null);

    const params = useParams();


    async function getResults() {
        const results = await request("GET", `/getTeamResults/${params.id}`, {}, applHeaders);

        setResults(
            await Promise.all(results.data.map(async day => ({
                ...day,
                matches: await getMatchesWithImg(day.matches)
            })))
        )
    }


    useEffect(() => {
        getResults();
    }, []);


    return (
        <div>
            <div className="results_header"><p>{`Результаты команды ${unFillSpaces(params.id)}`}</p></div>
            {results !== null ?
                <div className="results">
                    {results.map((day) =>
                        <ResultMaker day={day} key={day.date} />
                    )}
                </div>
                :
                <Preloader />
            }
        </div>
    );
}

export default TeamResults;