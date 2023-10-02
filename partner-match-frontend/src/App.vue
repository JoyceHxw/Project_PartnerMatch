<!-- <script setup lang="ts">
import BasicLayout from './layouts/BasicLayout.vue';

</script>

<template>
    <BasicLayout />
</template>

<style scoped>
</style> -->


<script setup lang="ts">
import {computed} from "vue";
import {useRoute} from "vue-router";

let route = useRoute();
const default_layout = "default"
const isRouterAlive = true
const layout = computed(() => {
    // console.log(route.meta.layout);
    return (route.meta.layout || default_layout) + '-layout'
})

</script>
<template>
    <div id="app">
        <component :is="layout">
            <router-view v-if="isRouterAlive"/>
        </component>
    </div>
</template>
