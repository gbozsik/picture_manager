<template>

    <div class="home">
        <vue-single-upload class="file-upload"
                           ref="singleUpload"
                           @finished="onUploadFinished()"
                           @removed="removeImage" >
        </vue-single-upload>

        <image-list class="mt-5" ref="imagelist"></image-list>

    </div>

</template>

<script lang="ts">
    import {Component, Prop, Vue} from 'vue-property-decorator';
    import VueSingleUpload from '@/components/VueSingleUpload.vue';
    import ImageList from '@/components/ImageList.vue'; // @ is an alias to /src

    @Component({
        components: {
            VueSingleUpload,
            ImageList,
        },
    })
    export default class Home extends Vue {

        public fileName: string | null = null;
        public thumbnailUrl: string | null = null;

        @Prop() private msg!: string;
        @Prop() private showDetails!: boolean | undefined;


        private async onUploadFinished() {
            console.log("UploadFinished");
            (this.$refs.imagelist as ImageList).refresh();
        }

        removeImage(file: File) {
            console.log("removed file", file.size)
        }

    }
</script>

<style lang="scss">
    .home {
        .uploader-dropzone {
            background-color: aliceblue;
        }

    }

</style>
