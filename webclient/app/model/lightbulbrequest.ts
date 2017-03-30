class HsvRequest {
    hue: number;
    sat: number;
    brightness: number;
}
class CtRequest {
    ct: number;
    brightness: number;
}
class PowerRequest {
    power: string;
}
class NameRequest {
    name: string;
}
export class LightbulbRequest {
    hsvRequest?: HsvRequest;
    ctRequest?: CtRequest;
    powerRequest?: PowerRequest;
    nameRequest?: NameRequest;
    requestTime?: number;
}