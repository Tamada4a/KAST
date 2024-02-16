export function getServerUrl() {
    return "https://localhost:8080";
}


export function getClientUrl() {
    return "https://localhost:3000";
}


export function getClientDomain() {
    let clientUrl = getClientUrl();
    let startIdx = clientUrl.lastIndexOf("/") + 1;
    return clientUrl.substring(startIdx);
}