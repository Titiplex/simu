<template>
    <div>
        <button @click="toggleModal">
            <img :src="building.imageUrl" :alt="building.type" class="building" :style="buildingStyle" />
        </button>
        <Modal id="modal" v-if="isModalOpen" 
               :building="building" 
               @close="toggleModal"
                />
    </div>
</template>

<script>
import Modal from "@/components/Modal.vue";

export default {
    name: 'Building',
    components: {
        Modal
    },
    props: ['building'],
    data() {
        return {
            isModalOpen: false
        };
    },
    computed: {
        buildingStyle() {
            return {
                top: `${this.building.position.y}px`, // Position verticale
                left: `${this.building.position.x}px` // Position horizontale
            };
        }
    },
    methods: {
        toggleModal() {
            if(!this.isModalOpen) {
                this.isModalOpen = true;
                setTimeout(() => {
                    document.getElementById('modal').classList.toggle('translate');
                }, 100);
            } else {
                setTimeout(() => {
                    document.getElementById('modal').classList.toggle('translate');
                }, 100);
                setTimeout(() => {
                    this.isModalOpen = !false;
                }, 700);
            }
        },
    }
};
</script>

<style scoped>
.building {
    position: absolute;
    width: 400px;
    height: 400px;
    z-index: 10;
}
</style>