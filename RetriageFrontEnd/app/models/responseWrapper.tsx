export interface ResponseWrapper<T> {
    httpStatus : number;
    error: string;
    data?: T;
}