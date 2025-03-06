<template>
    <div class="map-container">
        <img src="@/assets/map.png" alt="Map" class="map" />
        <!-- <Building v-for="(building, index) in buildings" :key="index" :building="building" /> -->
    </div>
</template>

<script>
import Building from '@/components/Building.vue';

export default {
    name: 'Map',
    components: {
        Building
    },
    props: ['money'],
    data() {
        return {
            buildings: [] // Liste des bâtiments
        };
    },
    methods: {
        // addBuilding(building) {
        //     this.buildings.push(building); // Ajouter un bâtiment à la carte
        // }
    }
};
</script>

<style scoped>
.map-container {
    position: relative;
    width: 100%;
    height: 100%;
}

.map {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.building {
    position: absolute;
    width: 50px;
    height: 50px;
    border: 2px solid black;
}
</style>