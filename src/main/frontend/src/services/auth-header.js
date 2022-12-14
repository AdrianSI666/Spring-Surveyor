export default function authHeader() {
    const accessToken = JSON.parse(sessionStorage.getItem('accessToken'))
    if(accessToken) {
        return { Authorization: 'Bearer ' + accessToken }
    } else {
        return {}
    }
}