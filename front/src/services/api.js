import axios from 'axios';


const API_URL = 'http://localhost:3000/';

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
            const response = await axios.post(`${API_URL}/hospitals/${building}`);
            return response.data;
        } catch (error) {
            console.error('Erreur lors de l’ajout du bâtiment:', error);
        }
    },
};
