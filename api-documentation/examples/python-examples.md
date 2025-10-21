# 🐍 Exemples Python - API MedHead

Ce fichier contient des exemples d'utilisation de l'API MedHead avec Python.

## 🚀 Installation des Dépendances

```bash
pip install requests
# ou
pip install httpx
# ou
pip install aiohttp  # pour les requêtes asynchrones
```

## 📦 Configuration Initiale

### 1. Configuration avec requests
```python
import requests
import json
from typing import Dict, Any, Optional

class MedHeadAPI:
    def __init__(self, base_url: str = "http://localhost:8080"):
        self.base_url = base_url
        self.session = requests.Session()
        self.session.headers.update({
            'Content-Type': 'application/json',
            'User-Agent': 'MedHead-Python-Client/1.0'
        })
    
    def _make_request(self, method: str, endpoint: str, **kwargs) -> requests.Response:
        """Effectue une requête HTTP avec gestion d'erreurs"""
        url = f"{self.base_url}{endpoint}"
        try:
            response = self.session.request(method, url, **kwargs)
            response.raise_for_status()
            return response
        except requests.exceptions.RequestException as e:
            print(f"Erreur de requête: {e}")
            raise
```

### 2. Configuration avec httpx (moderne)
```python
import httpx
import asyncio
from typing import Dict, Any

class AsyncMedHeadAPI:
    def __init__(self, base_url: str = "http://localhost:8080"):
        self.base_url = base_url
        self.client = httpx.AsyncClient(
            headers={
                'Content-Type': 'application/json',
                'User-Agent': 'MedHead-Async-Python-Client/1.0'
            },
            timeout=30.0
        )
    
    async def __aenter__(self):
        return self
    
    async def __aexit__(self, exc_type, exc_val, exc_tb):
        await self.client.aclose()
```

## 🏥 Allocation de Lits d'Hôpitaux

### 1. Allocation basique
```python
def allocate_hospital(api: MedHeadAPI, specialty: str, latitude: float, longitude: float) -> Dict[str, Any]:
    """Alloue un lit d'hôpital pour un patient"""
    payload = {
        "specialty": specialty,
        "latitude": latitude,
        "longitude": longitude
    }
    
    try:
        response = api._make_request('POST', '/api/allocate', json=payload)
        return response.json()
    except requests.exceptions.RequestException as e:
        print(f"Erreur d'allocation: {e}")
        return None

# Utilisation
api = MedHeadAPI()
result = allocate_hospital(api, "Cardiology", 53.3976314, -2.1829641)
if result:
    print(f"Hôpital recommandé: {result['hospital_name']}")
    print(f"Distance: {result['distance_km']} km")
    print(f"Temps estimé: {result['estimated_time_minutes']} minutes")
```

### 2. Allocation avec gestion d'erreurs complète
```python
def allocate_hospital_with_error_handling(api: MedHeadAPI, specialty: str, latitude: float, longitude: float) -> Dict[str, Any]:
    """Alloue un lit d'hôpital avec gestion d'erreurs détaillée"""
    payload = {
        "specialty": specialty,
        "latitude": latitude,
        "longitude": longitude
    }
    
    try:
        response = api._make_request('POST', '/api/allocate', json=payload)
        return {
            "success": True,
            "data": response.json(),
            "status_code": response.status_code,
            "response_time": response.elapsed.total_seconds() * 1000
        }
    except requests.exceptions.HTTPError as e:
        error_info = {
            "success": False,
            "status_code": e.response.status_code,
            "error": str(e),
            "response": e.response.text if e.response else None
        }
        
        # Gestion spécifique des erreurs
        if e.response.status_code == 400:
            print("❌ Erreur de validation des paramètres")
        elif e.response.status_code == 404:
            print("❌ Aucun hôpital trouvé pour cette spécialité")
        elif e.response.status_code == 500:
            print("❌ Erreur serveur interne")
        else:
            print(f"❌ Erreur HTTP {e.response.status_code}")
        
        return error_info
    except requests.exceptions.RequestException as e:
        return {
            "success": False,
            "error": str(e),
            "error_type": "RequestException"
        }
```

### 3. Allocation asynchrone
```python
async def async_allocate_hospital(api: AsyncMedHeadAPI, specialty: str, latitude: float, longitude: float) -> Dict[str, Any]:
    """Alloue un lit d'hôpital de manière asynchrone"""
    payload = {
        "specialty": specialty,
        "latitude": latitude,
        "longitude": longitude
    }
    
    try:
        response = await api.client.post(f"{api.base_url}/api/allocate", json=payload)
        response.raise_for_status()
        return response.json()
    except httpx.HTTPStatusError as e:
        print(f"Erreur HTTP {e.response.status_code}: {e.response.text}")
        return None
    except httpx.RequestError as e:
        print(f"Erreur de requête: {e}")
        return None

# Utilisation
async def main():
    async with AsyncMedHeadAPI() as api:
        result = await async_allocate_hospital(api, "Cardiology", 53.3976314, -2.1829641)
        if result:
            print(f"Hôpital: {result['hospital_name']}")

# Exécution
asyncio.run(main())
```

## 🔍 Tests de Diagnostic

### 1. Test de santé
```python
def check_api_health(api: MedHeadAPI) -> bool:
    """Vérifie que l'API est opérationnelle"""
    try:
        response = api._make_request('GET', '/api/health')
        print(f"✅ API Status: {response.text}")
        return True
    except requests.exceptions.RequestException as e:
        print(f"❌ API non disponible: {e}")
        return False
```

### 2. Suite de tests complète
```python
def run_diagnostic_tests(api: MedHeadAPI):
    """Exécute une suite de tests de diagnostic"""
    print("🔍 Tests de diagnostic MedHead")
    print("=" * 40)
    
    # Test de santé
    print("1. Test de santé...")
    health_ok = check_api_health(api)
    
    # Test d'allocation
    print("2. Test d'allocation...")
    try:
        result = allocate_hospital(api, "Cardiology", 53.3976314, -2.1829641)
        if result:
            print(f"✅ Allocation réussie: {result['hospital_name']}")
        else:
            print("❌ Allocation échouée")
    except Exception as e:
        print(f"❌ Erreur d'allocation: {e}")
    
    # Test de diagnostic automatique
    print("3. Test automatique...")
    try:
        response = api._make_request('GET', '/api/test')
        print(f"✅ Test automatique: {response.text}")
    except Exception as e:
        print(f"❌ Test automatique échoué: {e}")
```

## 👥 Gestion des Patients (Authentification)

### 1. Configuration avec authentification
```python
class AuthenticatedMedHeadAPI(MedHeadAPI):
    def __init__(self, base_url: str = "http://localhost:8080", jwt_token: str = None):
        super().__init__(base_url)
        if jwt_token:
            self.session.headers.update({'Authorization': f'Bearer {jwt_token}'})
    
    def set_auth_token(self, token: str):
        """Définit le token JWT pour l'authentification"""
        self.session.headers.update({'Authorization': f'Bearer {token}'})
```

### 2. Obtenir les statistiques patients
```python
def get_patient_statistics(api: AuthenticatedMedHeadAPI) -> Dict[str, Any]:
    """Récupère les statistiques patients anonymisées"""
    try:
        response = api._make_request('GET', '/api/patients/statistics')
        return response.json()
    except requests.exceptions.RequestException as e:
        print(f"Erreur lors de la récupération des statistiques: {e}")
        return None

# Utilisation
api = AuthenticatedMedHeadAPI(jwt_token="YOUR_JWT_TOKEN")
stats = get_patient_statistics(api)
if stats:
    print(f"Total patients: {stats.get('totalPatients', 0)}")
    print(f"Patients anonymisés: {stats.get('anonymizedPatients', 0)}")
```

### 3. Anonymiser tous les patients
```python
def anonymize_all_patients(api: AuthenticatedMedHeadAPI) -> str:
    """Anonymise tous les patients non-anonymisés"""
    try:
        response = api._make_request('POST', '/api/patients/anonymize-all')
        return response.text
    except requests.exceptions.RequestException as e:
        print(f"Erreur lors de l'anonymisation: {e}")
        return None
```

## 📊 Tests de Performance

### 1. Test de charge simple
```python
import time
import concurrent.futures
from typing import List, Dict, Any

def load_test(api: MedHeadAPI, concurrent_requests: int = 10) -> List[Dict[str, Any]]:
    """Effectue un test de charge avec des requêtes simultanées"""
    print(f"🚀 Test de charge avec {concurrent_requests} requêtes simultanées")
    
    def make_request(request_id: int) -> Dict[str, Any]:
        start_time = time.time()
        try:
            result = allocate_hospital(api, "Cardiology", 53.3976314, -2.1829641)
            end_time = time.time()
            return {
                "request_id": request_id,
                "success": True,
                "duration": (end_time - start_time) * 1000,
                "result": result
            }
        except Exception as e:
            end_time = time.time()
            return {
                "request_id": request_id,
                "success": False,
                "duration": (end_time - start_time) * 1000,
                "error": str(e)
            }
    
    start_time = time.time()
    
    with concurrent.futures.ThreadPoolExecutor(max_workers=concurrent_requests) as executor:
        futures = [executor.submit(make_request, i) for i in range(concurrent_requests)]
        results = [future.result() for future in concurrent.futures.as_completed(futures)]
    
    end_time = time.time()
    total_duration = (end_time - start_time) * 1000
    
    # Analyse des résultats
    successful = [r for r in results if r['success']]
    failed = [r for r in results if not r['success']]
    
    print(f"📊 Résultats du test de charge:")
    print(f"   Durée totale: {total_duration:.2f}ms")
    print(f"   Requêtes réussies: {len(successful)}")
    print(f"   Requêtes échouées: {len(failed)}")
    print(f"   Taux de succès: {len(successful) / len(results) * 100:.2f}%")
    
    if successful:
        avg_duration = sum(r['duration'] for r in successful) / len(successful)
        print(f"   Temps moyen par requête: {avg_duration:.2f}ms")
    
    return results
```

### 2. Test de charge avec métriques détaillées
```python
import threading
from collections import defaultdict

class LoadTester:
    def __init__(self, api: MedHeadAPI):
        self.api = api
        self.results = []
        self.lock = threading.Lock()
    
    def make_request(self, request_id: int) -> Dict[str, Any]:
        """Effectue une requête d'allocation"""
        start_time = time.time()
        try:
            result = allocate_hospital(self.api, "Cardiology", 53.3976314, -2.1829641)
            end_time = time.time()
            duration = (end_time - start_time) * 1000
            
            with self.lock:
                self.results.append({
                    "request_id": request_id,
                    "success": True,
                    "duration": duration,
                    "timestamp": start_time,
                    "result": result
                })
            
            return True
        except Exception as e:
            end_time = time.time()
            duration = (end_time - start_time) * 1000
            
            with self.lock:
                self.results.append({
                    "request_id": request_id,
                    "success": False,
                    "duration": duration,
                    "timestamp": start_time,
                    "error": str(e)
                })
            
            return False
    
    def run_load_test(self, duration_seconds: int = 30, requests_per_second: int = 5):
        """Exécute un test de charge pendant une durée spécifiée"""
        print(f"🚀 Test de charge détaillé ({duration_seconds}s, {requests_per_second} req/s)")
        
        start_time = time.time()
        end_time = start_time + duration_seconds
        request_count = 0
        
        def request_worker():
            nonlocal request_count
            while time.time() < end_time:
                request_count += 1
                self.make_request(request_count)
                time.sleep(1.0 / requests_per_second)
        
        # Lancer le test
        thread = threading.Thread(target=request_worker)
        thread.start()
        thread.join()
        
        # Analyser les résultats
        self.analyze_results(start_time, end_time)
    
    def analyze_results(self, start_time: float, end_time: float):
        """Analyse les résultats du test de charge"""
        total_duration = (end_time - start_time) * 1000
        successful = [r for r in self.results if r['success']]
        failed = [r for r in self.results if not r['success']]
        
        print(f"📊 Résultats du test de charge détaillé:")
        print(f"   Durée totale: {total_duration:.2f}ms")
        print(f"   Requêtes totales: {len(self.results)}")
        print(f"   Requêtes réussies: {len(successful)}")
        print(f"   Requêtes échouées: {len(failed)}")
        
        if self.results:
            success_rate = len(successful) / len(self.results) * 100
            print(f"   Taux de succès: {success_rate:.2f}%")
            
            if successful:
                durations = [r['duration'] for r in successful]
                avg_duration = sum(durations) / len(durations)
                min_duration = min(durations)
                max_duration = max(durations)
                
                print(f"   Temps de réponse moyen: {avg_duration:.2f}ms")
                print(f"   Temps de réponse min: {min_duration:.2f}ms")
                print(f"   Temps de réponse max: {max_duration:.2f}ms")
            
            requests_per_second = len(self.results) / (total_duration / 1000)
            print(f"   Requêtes par seconde: {requests_per_second:.2f}")

# Utilisation
api = MedHeadAPI()
tester = LoadTester(api)
tester.run_load_test(duration_seconds=30, requests_per_second=5)
```

## 🔄 Monitoring en Temps Réel

### 1. Monitoring continu
```python
import signal
import sys

class MedHeadMonitor:
    def __init__(self, api: MedHeadAPI, interval: int = 30):
        self.api = api
        self.interval = interval
        self.running = False
        self.stats = {
            'total_requests': 0,
            'successful_requests': 0,
            'failed_requests': 0,
            'total_response_time': 0
        }
    
    def check_health(self) -> bool:
        """Vérifie la santé de l'API"""
        try:
            start_time = time.time()
            response = self.api._make_request('GET', '/api/health')
            end_time = time.time()
            response_time = (end_time - start_time) * 1000
            
            self.stats['total_requests'] += 1
            self.stats['successful_requests'] += 1
            self.stats['total_response_time'] += response_time
            
            print(f"✅ [{time.strftime('%Y-%m-%d %H:%M:%S')}] API opérationnelle ({response_time:.2f}ms)")
            return True
        except Exception as e:
            self.stats['total_requests'] += 1
            self.stats['failed_requests'] += 1
            print(f"❌ [{time.strftime('%Y-%m-%d %H:%M:%S')}] API non disponible: {e}")
            return False
    
    def start(self):
        """Démarre le monitoring"""
        if self.running:
            return
        
        self.running = True
        print(f"🔍 Démarrage du monitoring (intervalle: {self.interval}s)")
        
        def signal_handler(sig, frame):
            print('\n⏹️ Arrêt du monitoring...')
            self.stop()
            sys.exit(0)
        
        signal.signal(signal.SIGINT, signal_handler)
        
        while self.running:
            self.check_health()
            time.sleep(self.interval)
    
    def stop(self):
        """Arrête le monitoring"""
        self.running = False
        print("⏹️ Monitoring arrêté")
        self.print_stats()
    
    def print_stats(self):
        """Affiche les statistiques"""
        if self.stats['total_requests'] > 0:
            success_rate = self.stats['successful_requests'] / self.stats['total_requests'] * 100
            avg_response_time = self.stats['total_response_time'] / self.stats['successful_requests'] if self.stats['successful_requests'] > 0 else 0
            
            print("📊 Statistiques finales:")
            print(f"   Requêtes totales: {self.stats['total_requests']}")
            print(f"   Requêtes réussies: {self.stats['successful_requests']}")
            print(f"   Requêtes échouées: {self.stats['failed_requests']}")
            print(f"   Taux de succès: {success_rate:.2f}%")
            print(f"   Temps de réponse moyen: {avg_response_time:.2f}ms")

# Utilisation
api = MedHeadAPI()
monitor = MedHeadMonitor(api, interval=30)
monitor.start()
```

## 🧪 Tests Automatisés

### 1. Suite de tests complète
```python
class MedHeadTestSuite:
    def __init__(self, api: MedHeadAPI):
        self.api = api
        self.results = []
    
    def run_test(self, name: str, test_function):
        """Exécute un test et enregistre le résultat"""
        print(f"🧪 Exécution du test: {name}")
        start_time = time.time()
        
        try:
            result = test_function()
            duration = (time.time() - start_time) * 1000
            
            self.results.append({
                'name': name,
                'success': True,
                'duration': duration,
                'result': result
            })
            
            print(f"✅ {name} - Réussi ({duration:.2f}ms)")
            return result
        except Exception as e:
            duration = (time.time() - start_time) * 1000
            
            self.results.append({
                'name': name,
                'success': False,
                'duration': duration,
                'error': str(e)
            })
            
            print(f"❌ {name} - Échoué ({duration:.2f}ms): {e}")
            raise
    
    def run_all_tests(self):
        """Exécute tous les tests"""
        print("🧪 Démarrage de la suite de tests MedHead")
        print("=" * 50)
        
        # Test de santé
        self.run_test("Test de santé", lambda: self.api._make_request('GET', '/api/health').text)
        
        # Test d'allocation
        self.run_test("Test d'allocation Cardiology", lambda: allocate_hospital(self.api, "Cardiology", 53.3976314, -2.1829641))
        
        # Test d'allocation avec spécialité différente
        self.run_test("Test d'allocation Dermatology", lambda: allocate_hospital(self.api, "Dermatology", 51.5074, -0.1278))
        
        # Test de diagnostic
        self.run_test("Test de diagnostic", lambda: self.api._make_request('GET', '/api/test').text)
        
        # Test de gestion d'erreur
        self.run_test("Test de gestion d'erreur", lambda: self._test_error_handling())
        
        self.print_results()
    
    def _test_error_handling(self):
        """Test la gestion d'erreur avec une spécialité inexistante"""
        try:
            allocate_hospital(self.api, "NonExistentSpecialty", 53.3976314, -2.1829641)
            raise Exception("Erreur attendue non générée")
        except requests.exceptions.HTTPError as e:
            if e.response.status_code == 404:
                return "Erreur 404 correctement gérée"
            raise
    
    def print_results(self):
        """Affiche les résultats des tests"""
        print("\n📊 Résultats de la suite de tests")
        print("=" * 40)
        
        successful = [r for r in self.results if r['success']]
        failed = [r for r in self.results if not r['success']]
        total = len(self.results)
        
        print(f"Tests réussis: {len(successful)}/{total}")
        print(f"Tests échoués: {len(failed)}/{total}")
        print(f"Taux de succès: {len(successful) / total * 100:.2f}%")
        
        if failed:
            print("\n❌ Tests échoués:")
            for result in failed:
                print(f"   - {result['name']}: {result['error']}")
        
        if self.results:
            avg_duration = sum(r['duration'] for r in self.results) / len(self.results)
            print(f"Temps moyen par test: {avg_duration:.2f}ms")

# Exécution de la suite de tests
api = MedHeadAPI()
test_suite = MedHeadTestSuite(api)
test_suite.run_all_tests()
```

## 📝 Exemples d'Utilisation Pratique

### 1. Application de démonstration
```python
def demonstrate_allocation():
    """Démonstration de l'API MedHead"""
    print("🏥 Démonstration MedHead - Allocation de Lits")
    print("=" * 50)
    
    api = MedHeadAPI()
    
    test_cases = [
        {"specialty": "Cardiology", "location": "Manchester", "lat": 53.3976314, "lng": -2.1829641},
        {"specialty": "Dermatology", "location": "London", "lat": 51.5074, "lng": -0.1278},
        {"specialty": "Neurology", "location": "Birmingham", "lat": 52.4862, "lng": -1.8904}
    ]
    
    for test_case in test_cases:
        print(f"\n📍 Test: {test_case['specialty']} à {test_case['location']}")
        
        try:
            result = allocate_hospital(api, test_case['specialty'], test_case['lat'], test_case['lng'])
            if result:
                print(f"✅ Hôpital recommandé: {result['hospital_name']}")
                print(f"   Distance: {result['distance_km']} km")
                print(f"   Temps estimé: {result['estimated_time_minutes']} minutes")
                print(f"   Lits disponibles: {result['available_beds']}")
            else:
                print("❌ Aucun résultat")
        except Exception as e:
            print(f"❌ Erreur: {e}")

# Exécution de la démonstration
demonstrate_allocation()
```

### 2. Script de monitoring avec alertes
```python
class AlertingMonitor(MedHeadMonitor):
    def __init__(self, api: MedHeadAPI, interval: int = 30, alert_threshold: float = 1000):
        super().__init__(api, interval)
        self.alert_threshold = alert_threshold
        self.consecutive_failures = 0
        self.alert_threshold_failures = 3
    
    def check_health(self) -> bool:
        """Vérifie la santé avec alertes"""
        success = super().check_health()
        
        if not success:
            self.consecutive_failures += 1
            if self.consecutive_failures >= self.alert_threshold_failures:
                self.send_alert(f"API non disponible depuis {self.consecutive_failures} vérifications")
        else:
            self.consecutive_failures = 0
        
        return success
    
    def send_alert(self, message: str):
        """Envoie une alerte (à personnaliser selon vos besoins)"""
        print(f"🚨 ALERTE: {message}")
        # Ici vous pourriez envoyer un email, une notification Slack, etc.

# Utilisation
api = MedHeadAPI()
monitor = AlertingMonitor(api, interval=30)
monitor.start()
```

---

**Note** : Assurez-vous d'avoir installé les dépendances nécessaires :
```bash
pip install requests httpx aiohttp
```

Et que l'application MedHead est démarrée :
```bash
cd /home/hedi/projects/medhead/docker
docker-compose up -d
```
