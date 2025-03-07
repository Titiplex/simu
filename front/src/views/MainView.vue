<template>
    <div class="main-view">
        <!-- <Hospitals /> -->
        <MoneyBar :money="money" />
        <Map :money="money" @add-building="addBuilding" @open="openModal" />
        <button @click="handleAddBuilding" class="add-building-button">Ajouter un hôpital: {{ buildings.length == 0 ?
            "100" : Math.floor(Math.pow(buildings.length, 1.5) * 100) }}</button>
        <!-- <Building v-for="(building, index) in buildings" :key="index" :building="building" @open="openModal" /> -->
        <Modal v-if="isModalOpen" :building="selectedBuilding" @close="isModalOpen = false" />
    </div>
</template>

<script>
import MoneyBar from '@/components/MoneyBar.vue';
import Map from '@/components/Map.vue';
import Building from '@/components/Building.vue';
import Modal from '@/components/Modal.vue';
import Hospitals from '@/components/Hospitals.vue';

export default {
    name: 'MainView',
    components: {
        MoneyBar,
        Map,
        Building,
        Modal,
        Hospitals
    },
    data() {
        return {
            money: 1000, // Argent initial
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
        openModal(element) {
            // Récupérer l'ID du bâtiment depuis l'élément
            const buildingId = element.dataset.buildingId;
            // Trouver le bâtiment correspondant dans la liste
            const building = this.buildings.find(b => b.id === buildingId);

            if (building) {
                this.selectedBuilding = building;
                this.isModalOpen = true;
            }
        },

        addBuilding(building) {
            const cost = Math.floor(Math.pow(this.buildings.length, 1.5) * 100);
            if (this.money >= cost) {
                const plot = document.getElementById(`city-${building.row}-${building.col}`);
                if (plot) {
                    plot.src = building.imageUrl;
                    plot.dataset.buildingId = building.id; // Stocker l'ID du bâtiment
                    plot.classList.add('hospital');
                    this.buildings.push(building);
                    this.money -= cost;
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
            const cost = Math.floor(Math.pow(this.buildings.length, 1.5) * 100);
            if (this.money < cost) {
                alert("Pas assez d'argent !");
                return;
            }
            const position = this.findAvailablePosition();

            if (!position) {
                alert("Impossible de construire plus d'hôpitaux - plus de place disponible !");
                return;
            }

            const services = [
                {
                    name: 'Service 1',
                    level: 1,
                    capacity: 50,
                    occupation: 0,
                    earningPerHealed: 2,
                    lossPerSecond: 0,
                    totalDeaths: 0,
                    totalHealed: 0
                },
                {
                    name: 'Service 2',
                    level: 1,
                    capacity: 30,
                    occupation: 2,
                    earningPerHealed: 3,
                    lossPerSecond: 2,
                    totalDeaths: 0,
                    totalHealed: 0
                },
                {
                    name: 'Service 3',
                    level: 1,
                    capacity: 20,
                    occupation: 10,
                    earningPerHealed: 3,
                    lossPerSecond: 2,
                    totalDeaths: 0,
                    totalHealed: 0
                },
                {
                    name: 'Service 4',
                    level: 1,
                    capacity: 10,
                    occupation: 0,
                    earningPerHealed: 3,
                    lossPerSecond: 2,
                    totalDeaths: 0,
                    totalHealed: 0
                }


            ];

            const buildingId = `Hôpital${this.buildings.length + 1}`;
            services.forEach(service => service.buildingId = buildingId);

            const totalCapacity = services.reduce((sum, s) => sum + s.capacity, 0);
            const totalOccupation = services.reduce((sum, s) => sum + s.occupation, 0);
            const totalEarningPerSecond = services.reduce((sum, s) => sum + s.earningPerHealed * s.occupation, 0);
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

            // Ajouter le bâtiment à la liste des bâtiments
            this.buildings.push(newBuilding);

            // Appeler la méthode d'ajout avec le nouveau bâtiment
            this.addBuilding(newBuilding);
        },
        updateMoney() {
            let totalEarnings = 0;
            let totalLosses = 0;

            this.buildings.forEach(building => {
                building.rent = building.capacity * 0.1;
                totalEarnings += building.earningPerSecond;
                totalLosses += building.lossPerSecond + building.rent;
            });

            // Mettre à jour l'argent en fonction des gains et pertes
            this.money += totalEarnings - totalLosses;
        }
    },
    mounted() {
        // Lancer la mise à jour automatique de l'argent toutes les secondes
        this.moneyInterval = setInterval(this.updateMoney, 1000);
    },
    beforeUnmount() {
        // Nettoyer l'intervalle quand le composant est détruit
        clearInterval(this.moneyInterval);
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

.add-building-button:hover {
    background-color: darkgreen;
}
</style>
