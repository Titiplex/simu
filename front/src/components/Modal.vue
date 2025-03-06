<template>
    <div class="modal-overlay">
        <div class="modal-content">
            <h2>{{ building.type }}</h2>
            <div class="building-info">
                <p>Capacité totale: {{ building.totalCapacity }}</p>
                <p>Occupation: {{ building.occupation }}</p>
                <p>Revenus par seconde: {{ building.earningPerSecond }}$</p>
                <p>Pertes par seconde: {{ building.lossPerSecond }}$</p>
            </div>
            <div class="services-info">
                <h3>Services</h3>
                <div v-for="service in building.services" :key="service.id" class="service">
                    <h4>{{ service.name }}</h4>
                    <p>Capacité: {{ service.capacity }}</p>
                    <p>Occupation: {{ service.occupation }}</p>
                </div>
            </div>
            <button class="close-button" v-on:click="close()">Fermer</button>
        </div>
    </div>
</template>

<script>
export default {
    name: 'Modal',
    props: {
        building: {
            type: Object,
            required: true
        }
    },
    methods: {
        close() {
            this.$emit('close');
        }
    }
};
</script>

<style scoped>
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 100;
}

.modal-content {
    background-color: white;
    padding: 20px;
    border-radius: 8px;
    max-width: 500px;
    width: 90%;
    color: black;
}

.building-info, .services-info {
    margin: 15px 0;
}

.service {
    border: 1px solid #ddd;
    padding: 10px;
    margin: 10px 0;
    border-radius: 4px;
}

.close-button {
    background-color: #ff4444;
    color: white;
    border: none;
    padding: 8px 16px;
    border-radius: 4px;
    cursor: pointer;
    margin-top: 10px;
}

.close-button:hover {
    background-color: #cc0000;
}
</style>