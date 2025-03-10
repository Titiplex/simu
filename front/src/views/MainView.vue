<template>
    <div class="main-view">
        <MoneyBar :money="money" />
        <Map :money="money" @add-building="addBuilding" @open="openModal" />
        <button @click="handleAddBuilding" class="add-building-button">Ajouter un hôpital: {{   Math.floor(Math.pow(buildings.length, 4)*100) }}</button>
        <!-- <Building v-for="(building, index) in buildings" :key="index" :building="building" @open="openModal" /> -->
        <Modal v-if="isModalOpen" :building="selectedBuilding" @close="isModalOpen = false" />
    </div>
</template>

<script>
import axios from 'axios';
import MoneyBar from '@/components/MoneyBar.vue';
import Map from '@/components/Map.vue';
import Building from '@/components/Building.vue';
import Modal from '@/components/Modal.vue';
import api from '@/services/api';

export default {
    name: 'MainView',
    components: {
        MoneyBar,
        Map,
        Building,
        Modal,
    },
    props: {
        buildings: {
            type: Array,
            required: true
        }
    },
    data() {
        return {
            money: 500, // Argent initial
            buildings: [], // Liste des bâtiments ajoutés
            isModalOpen: false,
            possiblePositions: [
                { row: 23, col: 23 },
                { row: 17, col: 17 },
                { row: 32, col: 23 },
                { row: 26, col: 29 },
                { row: 26, col: 17 },
                { row: 20, col: 11 },
                { row: 26, col: 20 },
                { row: 29, col: 26 },
                { row: 17, col: 23 }
            ],
        };
    },
    methods: {
        updateBuildings(updatedBuildings) {
            this.buildings = updatedBuildings;
        },
        openModal(element) {
            // Récupérer l'ID du bâtiment depuis l'élément
            const buildingId = element.dataset.buildingId;
            // Trouver le bâtiment correspondant dans la liste
            const building = this.buildings.find(b => b.id == buildingId);

            if (building) {
                this.selectedBuilding = building;
                this.isModalOpen = true;
            }
        },
        async addBuildingToAPI(building) {
            try {
                // Appel de la méthode addBuilding de l'API
                const response = await api.addBuilding(building);
                return response; // Retourne les données de l'API
            } catch (error) {
                console.error('Erreur lors de l’ajout du bâtiment:', error);
            }
        },

        async addBuilding(building) {
            const cost = Math.floor(Math.pow(this.buildings.length, 4)*100);
            if (this.money >= cost) {
                const plot = document.getElementById(`city-${building.row}-${building.col}`);
                if (plot) {
                    plot.src = building.imageUrl;
                    plot.dataset.buildingId = building.id; // Stocker l'ID du bâtiment
                    plot.classList.add('hospital');

                    try {
                        // Appel API pour ajouter le bâtiment
                        await this.addBuildingToAPI(building);

                        // Ajout du bâtiment localement si l'API a réussi
                        this.buildings.push(building);
                        console.log('Bâtiment ajouté via l\'API:', building);
                        this.money -= cost;
                    } catch (error) {
                        console.error('Erreur lors de l\'ajout du bâtiment via l\'API:', error);
                    }
                }
            } else {
                alert("Pas assez d'argent !");
            }
        },

        findAvailablePosition() {
            return this.possiblePositions.find(pos => {
                const existingBuilding = this.buildings.find(b =>
                    b.row === pos.row && b.col === pos.col
                );
                return !existingBuilding;
            });
        },

        //CRÉER LES BUILDINDS AVEC LES SERVICES ASSOCIÉS
        handleAddBuilding() {
            const cost = Math.floor(Math.pow(this.buildings.length, 4)*100);
            if(this.money < cost) {
                alert("Pas assez d'argent !");
                return;
            }
            const position = this.findAvailablePosition();

            if (!position) {
                alert("Impossible de construire plus d'hôpitaux - plus de place disponible !");
                return;
            }

            let services = [
                {
                    name: 'Chirurgie',
                    level: 1,
                    capacity: 50,
                    occupation: 0,
                    earningPerHealed: 2,
                    lossPerSecond: 0,
                    totalDeaths: 0,
                    totalHealed: 0
                },
                {
                    name: 'Bloque',
                    level: 1,
                    capacity: 30,
                    occupation: 2,
                    earningPerHealed: 3,
                    lossPerSecond: 2,
                    totalDeaths: 0,
                    totalHealed: 0
                },
                {
                    name: 'Urgences',
                    level: 1,
                    capacity: 20,
                    occupation: 10,
                    earningPerHealed: 3,
                    lossPerSecond: 2,
                    totalDeaths: 0,
                    totalHealed: 0
                },
                {
                    name: 'Medecine',
                    level: 1,
                    capacity: 10,
                    occupation: 0,
                    earningPerHealed: 3,
                    lossPerSecond: 2,
                    totalDeaths: 0,
                    totalHealed: 0
                }


            ];

            const buildingId = this.buildings.length + 1;
            services.forEach(service => service.buildingId = buildingId);

            const totalCapacity = services.reduce((sum, s) => sum + s.capacity, 0);
            const totalOccupation = services.reduce((sum, s) => sum + s.occupation, 0);
            const totalEarningPerSecond = services.reduce((sum, s) => sum + s.earningPerHealed * s.occupation * s.level, 0);
            const totalLossPerSecond = services.reduce((sum, s) => sum + s.lossPerSecond * s.occupation, 0);

            const newBuilding = {
                id: buildingId,
                level: 1,
                capacity: totalCapacity,
                occupation: totalOccupation,
                earningPerSecond: totalEarningPerSecond,
                lossPerSecond: totalLossPerSecond,
                totalDeaths: 0,
                totalHealed: 0,
                row: position.row,
                col: position.col,
                rent: totalCapacity * 0.1,
                imageUrl: new URL('@/assets/parts/buildingTiles_041.png', import.meta.url).href,
                services
            };

            this.addBuilding(newBuilding);
        },
        updateMoney() {
            let totalEarnings = 0;
            let totalLosses = 0;

            this.buildings.forEach(building => {
                // Recalculer les gains pour chaque bâtiment en fonction de ses services
                console.log(building.services);
                building.earningPerSecond = building.services.reduce(
                    (sum, service) => sum + service.earningPerHealed * service.occupation * service.level,
                    0
                );
                console.log(building.earningPerSecond);
                
                // Recalculer les pertes pour chaque bâtiment en fonction de ses services
                building.lossPerSecond = building.services.reduce(
                    (sum, service) => sum + service.lossPerSecond * service.occupation,
                    0
                );

                // Calculer le loyer basé sur la capacité
                building.rent = building.capacity * 0.1;
                
                // Ajouter aux totaux
                totalEarnings += building.earningPerSecond;
                totalLosses += building.lossPerSecond + building.rent;
            });

            // Mettre à jour l'argent en fonction des gains et pertes
            this.money += totalEarnings - totalLosses;
        },

        async fetchAndUpdateHospitals() {
            try {
                const hospitals = await api.fetchHospitals();
                this.buildings = hospitals.map(hospital => ({
                    id: hospital.id,
                    level: 1,
                    capacity: hospital.services.reduce((sum, s) => sum + s.maxCapacity, 0),
                    occupation: hospital.services.reduce((sum, s) => sum + s.occupiedCapacity, 0),
                    services: hospital.services.map(service => ({
                        name: service.name,
                        level: 1,
                        capacity: service.maxCapacity,
                        occupation: service.occupiedCapacity,
                        earningPerHealed: 3,
                        lossPerSecond: 2,
                        totalDeaths: 0,
                        totalHealed: 0
                    })),
                    earningPerSecond: 0,
                    lossPerSecond: 0,
                    totalDeaths: 0,
                    totalHealed: 0,
                    imageUrl: new URL('@/assets/parts/buildingTiles_041.png', import.meta.url).href
                }));
            } catch (error) {
                console.error('Erreur lors de la mise à jour des hôpitaux:', error);
            }
        }
    },
    mounted() {
        // Lancer la mise à jour automatique de l'argent toutes les secondes
        this.moneyInterval = setInterval(this.updateMoney, 1000);

        // Chargement initial
        this.fetchAndUpdateHospitals();

        // Mise à jour toutes les 5 secondes
        this.updateInterval = setInterval(() => {
            this.fetchAndUpdateHospitals();
        }, 5000);
    },
    beforeUnmount() {
        // Nettoyer l'intervalle quand le composant est détruit
        clearInterval(this.moneyInterval);

        // Nettoyage de l'intervalle
        if (this.updateInterval) {
            clearInterval(this.updateInterval);
        }
    }
};
</script>

<style scoped>
.main-view {
    position: relative;
    width: 100%;
    height: 100vh;
    overflow: hidden;
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
</style>
