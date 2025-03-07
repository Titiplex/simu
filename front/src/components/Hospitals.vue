<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import api from '@/services/api.js';

const hospitals = ref([]);
let intervalId = null;

// Fonction pour récupérer les hôpitaux
const fetchHospitals = async () => {
    try {
        const data = await api.fetchHospitals();
        hospitals.value = data; // Met à jour les hôpitaux avec les nouvelles données
    } catch (error) {
        console.error("Erreur lors de la récupération des hôpitaux :", error.response || error.message);
    }
};


// Démarrer la récupération automatique
onMounted(() => {
    fetchHospitals(); // Appel initial
    intervalId = setInterval(fetchHospitals, 1000); // Rafraîchit chaque seconde
});

// Nettoyage à la destruction du composant
onUnmounted(() => {
    clearInterval(intervalId);
});
</script>

<template>
    <div>
        <h1>Liste des hôpitaux</h1>
        <ul>
            <li v-for="hospital in hospitals" :key="hospital.id">
                <strong>{{ hospital.name }}</strong>
                <ul>
                    <li v-for="service in hospital.services" :key="service.name">
                        <strong>{{ service.name }}</strong> - Charge actuelle: {{ service.currentLoad }}/{{
                            service.maxCapacity }}
                    </li>
                </ul>
            </li>
        </ul>
    </div>
</template>
