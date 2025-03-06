<template>
    <div class="main-view">
        <MoneyBar :money="money" />
        <Map :money="money" @add-building="addBuilding" />
        <button @click="handleAddBuilding" class="add-building-button">Ajouter un bâtiment</button>
        <!-- Afficher les bâtiments ajoutés -->
        <Building v-for="(building, index) in buildings" :key="building.id" :building="building" />
    </div>
</template>

<script>
import MoneyBar from '@/components/MoneyBar.vue';
import Map from '@/components/Map.vue';
import Building from '@/components/Building.vue'; // Importer Building

export default {
    name: 'MainView',
    components: {
        MoneyBar,
        Map,
        Building // Ajouter Building à la liste des composants
    },
    data() {
        return {
            money: 1000, // Argent initial
            buildings: [] // Liste des bâtiments ajoutés
        };
    },
    methods: {
        addBuilding(building) {
            if (this.money >= building.cost) {
                this.buildings.push(building); // Ajouter le bâtiment à la liste
                this.money -= building.cost; // Déduire l'argent pour l'achat du bâtiment
            } else {
                alert("Pas assez d'argent !");
            }
        },
        handleAddBuilding() {
            const newBuilding = {
                id: Date.now(),
                type: 'building1',
                //position aléatoire sur l'écran
                position: { x: Math.random() * 1500, y: Math.random() * 600 },
                //position fixe
                //position: { x: 200, y: 200 },
                cost: 100, // Coût du bâtiment
                imageUrl: new URL('@/assets/buildings/building1.png', import.meta.url).href // Correction de l'importation de l'image
            };
            this.addBuilding(newBuilding);
        }
    }
};
</script>

<style scoped>
.main-view {
    position: relative;
    width: 100%;
    height: 100vh;
}

.add-building-button {
    position: fixed;
    bottom: 20px;
    right: 20px;
    padding: 10px 20px;
    background-color: green;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    z-index: 100;
}

.add-building-button:hover {
    background-color: darkgreen;
}
</style>
