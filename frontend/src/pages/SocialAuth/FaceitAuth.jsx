import Cookies from 'universal-cookie';
import { applHeaders, request } from "../../Utils/MyAxios";
import { getClientUrl } from '../../Utils/HostData';

// https://stackoverflow.com/a/59913241/14478725
function dec2hex(dec) {
    return (`0${dec.toString(16)}`).substr(-2);
}


function generateCodeVerifier() {
    let array = new Uint32Array(56 / 2);
    window.crypto.getRandomValues(array);
    return Array.from(array, dec2hex).join("");
}


function sha256(plain) {
    const encoder = new TextEncoder();
    const data = encoder.encode(plain);
    return window.crypto.subtle.digest("SHA-256", data);
}

function base64urlencode(a) {
    let str = "";
    let bytes = new Uint8Array(a);
    let len = bytes.byteLength;
    for (let i = 0; i < len; i++) {
        str += String.fromCharCode(bytes[i]);
    }
    return window.btoa(str)
        .replace(/\+/g, "-")
        .replace(/\//g, "_")
        .replace(/=+$/, "");
}


async function generateCodeChallengeFromVerifier(v) {
    let hashed = await sha256(v);
    let base64encoded = base64urlencode(hashed);
    return base64encoded;
}


function encodeGetParams(p) {
    return Object.entries(p).map(kv => kv.map(encodeURIComponent).join("=")).join("&");
}


function setVerifier(verifier) {
    const cookies = new Cookies(null, { path: '/' });
    cookies.set('verifier', verifier, { expires: new Date(new Date().getTime() + 300000) });
}

// PKCE аутентификация
export async function faceitAuth() {
    let verifier = generateCodeVerifier();
    let challenge = generateCodeChallengeFromVerifier(verifier);

    setVerifier(verifier);

    const authorizeUrl = "https://cdn.faceit.com/widgets/sso/index.html?";
    let urlGetParams = {
        client_id: await request("GET", "/getFaceitClientID", {}, applHeaders).then(res => res.data),
        response_type: "code",
        redirect_uri: `${getClientUrl()}/social-auth/faceit/`,
        code_challenge_method: "S256"
    }

    challenge.then((chall) => {
        urlGetParams["code_challenge"] = chall;
        window.open(authorizeUrl + encodeGetParams(urlGetParams));
    });
}