import React, { useState, useEffect } from "react";
import InfoPage from "../../components/InfoPage/InfoPage";
import { useParams, useLocation, useNavigate } from "react-router-dom";
import { applHeaders, request } from "../../Utils/MyAxios";
import { getStoredPlayerNick } from "../../Utils/Utils";
import Cookies from 'universal-cookie';


function SocialAuth() {
    const [infoLabelIndex, setInfoLabelIndex] = useState(0);
    const params = useParams();
    const cookies = new Cookies(null, { path: '/' });

    const labelText = {
        0: <p>Не закрывайте<br />страницу, получаем<br />нужную информацию</p>,
        1: <p>Информация<br />получена!</p>,
        2: <p>Ошибка при<br />обработке запроса,<br />попробуйте ещё раз!</p>
    }

    const location = useLocation();

    const navigate = useNavigate();


    useEffect(() => {
        getData();
    }, []);


    function delayedWindowRedirect() {
        navigate(`/player/${getStoredPlayerNick()}`)
    }


    function onSuccessRequest() {
        setInfoLabelIndex(1);
        setTimeout(delayedWindowRedirect, 1 * 1000);
    }


    function getVerifier() {
        return cookies.get('verifier');
    }


    async function discordAuth() {
        const urlParams = new URLSearchParams(location.hash.slice(1));
        const type = urlParams.get("token_type");
        const token = urlParams.get("access_token");

        const headers = {
            "Accept": "application/json",
            "Content-Type": "application/json",
            "Authorization": `${type} ${token}`
        };
        if (type !== "null" && type !== null && type !== undefined && token !== "null" && token !== null && token !== undefined) {
            try {
                let response = await request("GET", "https://discord.com/api/users/@me", {}, headers, "socialAuth");
                await request("POST", "/changeSocial", {
                    player: getStoredPlayerNick(),
                    link: response.data.username,
                    social: "Discord"
                });
                onSuccessRequest();
            } catch (err) {
                setInfoLabelIndex(2);
            }
        } else {
            setInfoLabelIndex(2);
        }
    }


    async function vkAuth() {
        // https://dev.vk.com/ru/api/access-token/authcode-flow-user#%D0%9E%D1%82%D0%BA%D1%80%D1%8B%D1%82%D0%B8%D0%B5%20%D0%B4%D0%B8%D0%B0%D0%BB%D0%BE%D0%B3%D0%B0%20%D0%B0%D0%B2%D1%82%D0%BE%D1%80%D0%B8%D0%B7%D0%B0%D1%86%D0%B8%D0%B8
        const urlParams = new URLSearchParams(location.search);
        const code = urlParams.get("code");

        if (code !== "null" && code !== null && code !== undefined) {
            try {
                await request("POST", `/setVKLink/${getStoredPlayerNick()}/${code}`, {}, applHeaders);
                onSuccessRequest();
            } catch (err) {
                setInfoLabelIndex(2);
            }
        } else {
            setInfoLabelIndex(2);
        }
    }


    async function steamAuth() {
        const urlParams = new URLSearchParams(location.search);
        const openid = urlParams.get("openid.claimed_id");

        if (openid !== "null" && openid !== null) {
            const steamId = openid.split('/')[5];
            try {
                await request("POST", "/changeSocial", {
                    player: getStoredPlayerNick(),
                    link: `https://steamcommunity.com/profiles/${steamId}`,
                    social: "Steam"
                });
                onSuccessRequest();
            } catch (err) {
                setInfoLabelIndex(2);
            }
        } else {
            setInfoLabelIndex(2);
        }
    }


    async function faceitAuth() {
        const urlParams = new URLSearchParams(location.search);
        const code = urlParams.get("code");
        if (code !== "null" && code !== null) {
            const data = {
                code: code,
                verifier: getVerifier()
            }
            try {
                await request("POST", `/setFaceitLink/${getStoredPlayerNick()}`, data, {}, applHeaders);
                onSuccessRequest();
            } catch (err) {
                setInfoLabelIndex(2);
            }
        } else {
            setInfoLabelIndex(2);
        }
    }


    function getData() {
        if (params.id === "discord") {
            discordAuth();
        } else if (params.id === "vk") {
            vkAuth();
        } else if (params.id === "steam") {
            steamAuth()
        } else if (params.id === "faceit") {
            faceitAuth();
        }
    }

    return (
        <div >
            <InfoPage>
                {labelText[infoLabelIndex]}
            </InfoPage>
        </div>

    )
}

export default SocialAuth;