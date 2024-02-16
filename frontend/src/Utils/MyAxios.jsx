import axios from "axios";
import Cookies from 'universal-cookie';
import { getServerUrl } from "./HostData";


axios.defaults.baseURL = getServerUrl();
axios.defaults.headers.post["Content-Type"] = "application/json";

const cookies = new Cookies(null, { path: '/' });


export function getAuthToken() {
    return cookies.get("authToken");
};


export function setAuthToken(token, expires = 1) {
    cookies.set("authToken", token, { expires: new Date(new Date().getTime() + expires * 3600000) });
};


export const applHeaders = {
    "Accept": "application/json",
    "Content-Type": "application/json"
};


export function request(method, url, data, headers = {}, responseType = "") {
    if (getAuthToken() !== null && getAuthToken() !== "null" && getAuthToken() !== "undefined" && getAuthToken() !== undefined && responseType !== "socialAuth") {
        headers["Authorization"] = `Bearer ${getAuthToken()}`;
    }

    if (responseType !== "" && responseType !== "socialAuth") {
        return axios({
            method: method,
            headers: headers,
            url: url,
            data: data,
            responseType: 'blob'
        });
    }

    return axios({
        method: method,
        headers: headers,
        url: url,
        data: data
    });
};