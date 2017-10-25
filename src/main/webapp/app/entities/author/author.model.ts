import { BaseEntity } from './../../shared';

export class Author implements BaseEntity {
    constructor(
        public id?: number,
        public name?: string,
        public mySize?: number,
        public nextVal?: boolean,
    ) {
        this.nextVal = false;
    }
}
