import axios from 'axios';

const API_URL = 'http://10.126.2.137:8080/api';

export default {
    async getBuildings(id) {
        try {
            const response = await axios.get(`${API_URL}/hospitals/${id}`);
            return response.data;
        } catch (error) {
            console.error('Erreur lors de la récupération des bâtiments:', error);
            return [];
        }
    },

    async addBuilding(building) {
        try {
            const response = await axios.post(`${API_URL}/hospitals`, building);
            return response.data;
        } catch (error) {
            if (error.response) {
                // Affiche la réponse complète de l'API, y compris le message d'erreur
                console.error('Erreur lors de l’ajout du bâtiment:', error.response.data);
            } else {
                console.error('Erreur lors de l’ajout du bâtiment:', error.message);
            }
        }
    },


    async updateMaxCapacity(buildingId, maxCapacity, serviceName) {
        try {
            const response = await axios.post(`${API_URL}/hospitals/${buildingId}/services`, {
                buildingId,
                maxCapacity,
                serviceName
            });
            return response.data;
        } catch (error) {
            console.error('Erreur lors de l’envoi du bâtiment:', error);
        }
    },

    async fetchHospitals() {
        try {
            // const response = await axios.get(`${API_URL}/hospitals`);
            const response = await axios.get(`${API_URL}/hospitals/hospitals`);

            console.log("api requested")
            console.log(response.data);
            return response.data;
        } catch (error) {
            console.error('Erreur lors de la récupération des hôpitaux:', error);
            return [];
        }
    },
};


