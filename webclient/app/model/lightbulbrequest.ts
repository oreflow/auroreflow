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
export class LightbulbRequest {
    hsv_request?: HsvRequest;
    ct_request?: CtRequest;
    power_request?: PowerRequest;
}