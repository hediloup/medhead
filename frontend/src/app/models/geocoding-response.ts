export interface GeocodingResponse {
  lat: number;
  lon: number;
  display_name: string;
  address?: {
    city?: string;
    country?: string;
    postcode?: string;
  };
}
