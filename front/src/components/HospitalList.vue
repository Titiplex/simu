<script>
import api from '@/api';

export default {
    data() {
        return {
            hospitals: null // Stocke les données JSON brutes
        };
    },
    async mounted() {
        this.hospitals = await api.fetchHospitals();
    }
};
</script>

<template>
    <div>
        <h1>Liste des hôpitaux (JSON brut)</h1>
        <pre v-if="hospitals">{{ JSON.stringify(hospitals, null, 2) }}</pre>
        <p v-else>Chargement des données...</p>
    </div>
</template>
