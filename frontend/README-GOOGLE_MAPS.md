Setup Google Maps API key for MedHead frontend

1) Create a Browser API key in Google Cloud Console
   - Go to https://console.cloud.google.com/apis/credentials
   - Create Credentials -> API key -> restrict to 'HTTP referrers' (e.g., http://localhost:4200/* and your production domain)
   - Restrict API key to "Maps JavaScript API" and "Directions API" / "Distance Matrix API" as needed.

2) Local development
   - Edit `src/environments/environment.ts` and set `googleMapsApiKey: '<YOUR_BROWSER_KEY>'`
   - Run `npm ci` then `npx ng serve` or `npx ng build`.

3) CI / Production
   - Add `GOOGLE_MAPS_API_KEY` as a GitHub secret in the repository settings.
   - The CI workflow will inject this secret into `src/environments/environment.prod.ts` before building.

Security note
- Keep API keys restricted by HTTP referrers to avoid misuse. The key will be visible in client-side bundles; consider a server-side proxy if you need stronger protection.
