import {useEffect} from "react";
import Cookies from "js-cookie";

export function SetDefaultCookie(key: string, value: string) {
    useEffect(() => {
        // Check if cookie is already set
        let cookie = Cookies.get(key);

        // If not set, define default user/role
        if (!cookie) {
            cookie = value;
            Cookies.set(key, cookie, {path: "/"});
        }
    })
}
export function getCookies(key: string){
    return Cookies.get(key) || "";
}
//TODO figure out how to set this method to be lowercase and still use Cookies.set
export function SetCookies(key: string, value: string) {
    useEffect(() => {
        Cookies.set(key, value, {path: "/"});
    })
}