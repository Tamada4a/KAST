import React, { useState } from "react";
import { useParams } from "react-router-dom";
import { useEffect } from "react";
import ResultMaker from "../../components/ResultMaker/ResultMaker";
import { applHeaders, request } from "../../Utils/MyAxios";
import { getMatchesWithImg } from "../../Utils/Utils";
import "../Results/Results.css";
import Preloader from "../../components/Preloader/Preloader";

function PlayerResults() {
    const [results, setResults] = useState(null);

    const params = useParams();

    async function getResults() {
        const results = await request("GET", `/getPlayerResults/${params.id}`, {}, applHeaders);

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
            <div className="results_header"><p>{`Результаты игрока ${params.id}`}</p></div>
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

export default PlayerResults;