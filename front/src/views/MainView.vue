<template>
    <div class="main-view">
        <MoneyBar :money="money" />
        <Map :money="money" @add-building="addBuilding" />
        <button @click="handleAddBuilding" class="add-building-button">Ajouter un hôpital</button>
        <Building v-for="(building, index) in buildings" :key="index" :building="building" @open="openModal"/>
        <Modal v-if="isModalOpen"
            :building="selectedBuilding"
            @close="isModalOpen = false"
        />
    </div>
</template>

<script>
import MoneyBar from '@/components/MoneyBar.vue';
import Map from '@/components/Map.vue';
import Building from '@/components/Building.vue';
import Modal from '@/components/Modal.vue';

export default {
    name: 'MainView',
    components: {
        MoneyBar,
        Map,
        Building,
        Modal
    },
    data() {
        return {
            money: 1000, // Argent initial
            buildings: [], // Liste des bâtiments ajoutés
            isModalOpen: false,
        };
    },
    methods: {
        openModal(building) {
            this.isModalOpen = false;
            console.log(building.id);
            this.selectedBuilding = building;
            this.isModalOpen = true;
        },

        //PUSH LES BUILDINGS DANS LA LISTE
        addBuilding(building) {
            if (this.money >= building.cost) {
                this.buildings.push(building); // Ajouter le bâtiment à la liste
                this.money -= building.cost; // Déduire l'argent pour l'achat du bâtiment
            } else {
                alert("Pas assez d'argent !");
            }
        },
        //CRÉER LES BUILDINDS AVEC LES SERVICES ASSOCIÉS
        handleAddBuilding() {
            console.log("ok");

            const services = [
                {
                    name: 'Service 1',
                    level: 1,
                    capacity: 50,
                    occupation: 0,
                    earningPerHealed: 5,
                    lossPerSecond: 0,
                    totalDeaths: 0,
                    totalHealed: 0
                },
                {
                    name: 'Service 2',
                    level: 1,
                    capacity: 30,
                    occupation: 0,
                    earningPerHealed: 5,
                    lossPerSecond: 1,
                    totalDeaths: 0,
                    totalHealed: 0
                },
                {
                    name: 'Service 3',
                    level: 1,
                    capacity: 30,
                    occupation: 0,
                    earningPerHealed: 5,
                    lossPerSecond: 1,
                    totalDeaths: 0,
                    totalHealed: 0
                },
                {
                    name: 'Service 4',
                    level: 1,
                    capacity: 30,
                    occupation: 0,
                    earningPerHealed: 5,
                    lossPerSecond: 1,
                    totalDeaths: 0,
                    totalHealed: 0
                },
                {
                    name: 'Service 5',
                    level: 1,
                    capacity: 30,
                    occupation: 0,
                    earningPerHealed: 5,
                    lossPerSecond: 1,
                    totalDeaths: 0,
                    totalHealed: 0
                }
            ];

            // Générer un ID unique pour le bâtiment
            const buildingId = `Hôpital${this.buildings.length + 1}`;

            // Associer le buildingId aux services
            services.forEach(service => service.buildingId = buildingId);

            // Calculer les valeurs totales à partir des services
            const totalCapacity = services.reduce((sum, s) => sum + s.capacity, 0);
            const totalOccupation = services.reduce((sum, s) => sum + s.occupation, 0);
            const totalEarningPerSecond = services.reduce((sum, s) => sum + s.earningPerSecond, 0);
            const totalLossPerSecond = services.reduce((sum, s) => sum + s.lossPerSecond, 0);

            const newBuilding = {
                id: buildingId,
                level: 1,
                Capacity: totalCapacity,
                occupation: totalOccupation,
                earningPerSecond: totalEarningPerSecond,
                lossPerSecond: totalLossPerSecond,
                totalDeaths: 0,
                totalHealed: 0,
                position: { x: Math.random() * 1500, y: Math.random() * 600 },
                cost: 100, // Coût du bâtiment
                imageUrl: new URL('@/assets/buildings/building1.png', import.meta.url).href,
                services
            };
            console.log(newBuilding);
            this.addBuilding(newBuilding);
        },
        updateMoney() {
            let totalEarnings = 0;
            let totalLosses = 0;

            this.buildings.forEach(building => {
                totalEarnings += building.earningPerSecond;
                totalLosses += building.lossPerSecond;
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
